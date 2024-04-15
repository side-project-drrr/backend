package com.drrr.domain.techblogpost.service;

import com.drrr.domain.techblogpost.cache.entity.RedisPostCategories;
import com.drrr.domain.techblogpost.cache.entity.RedisPostDynamicData;
import com.drrr.domain.techblogpost.cache.entity.RedisTechBlogPostsCategoriesStaticData;
import com.drrr.domain.techblogpost.cache.payload.RedisPostsContents;
import com.drrr.domain.techblogpost.dto.TechBlogPostCategoryDto;
import com.drrr.domain.techblogpost.repository.RedisPostCategoryStaticDataRepository;
import com.drrr.domain.techblogpost.repository.RedisPostDynamicDataRepository;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class RedisTechBlogPostService {
    private final RedisPostDynamicDataRepository redisPostDynamicDataRepository;
    private final RedisTemplate<String, Object> redisTemplate;


    private final RedisPostCategoryStaticDataRepository redisPostCategoryStaticDataRepository;

    public <T> Boolean hasCachedKeyByRange(final int page, final int size, final String key) {
        final int start = page * size;
        final int end = start + size;

        final Set<Object> techBlogPosts = redisTemplate.opsForZSet()
                .range(key, start, end);

        if (Objects.nonNull(techBlogPosts)) {
            return techBlogPosts.size() == size;
        }

        return false;
    }

    public RedisPostCategories findCacheSlicePostsInRedis(final int page, final int size, final String key) {
        final int start = page * size;
        final int end = start + size;

        final List<RedisTechBlogPostsCategoriesStaticData> staticData = Objects.requireNonNull(
                        redisTemplate.opsForZSet()
                                .range(key, start, end))
                .stream()
                .filter(Objects::nonNull)
                .map((data) -> (RedisTechBlogPostsCategoriesStaticData) data)
                .toList();

        boolean hasNext = false;

        if (!staticData.isEmpty()) {
            hasNext = staticData.get(staticData.size() - 1).hasNext();
        }

        //정적 정보 TTL 초기화
        redisTemplate.expire(key, 3600, TimeUnit.SECONDS);

        final List<Long> keys = staticData.stream()
                .map((post) -> post.redisTechBlogPostStaticData().id())
                .toList();

        //repository를 쓰면 HSET를 쉽게 객체로 변환해줌
        final Iterable<RedisPostDynamicData> postDynamicData = redisPostDynamicDataRepository.findAllById(keys);

        final Map<Long, RedisPostDynamicData> postDynamicDataMap = StreamSupport.stream(postDynamicData.spliterator(),
                        false)
                .collect(Collectors.toMap(RedisPostDynamicData::getPostId, Function.identity()));

        final List<RedisPostsContents> redisPostsContents = staticData.stream()
                .map(data -> RedisPostsContents.builder()
                        .redisTechBlogPostDynamicData(postDynamicDataMap.get(data.redisTechBlogPostStaticData().id()))
                        .redisTechBlogPostStaticData(data.redisTechBlogPostStaticData())
                        .redisCategories(data.redisCategories())
                        .build())
                .toList();

        //동적 정보 TTL 초기화
        postDynamicDataMap.forEach((keyValue, value) -> {
            redisPostDynamicDataRepository.deleteById(keyValue);
            redisPostDynamicDataRepository.save(value);
        });

        return RedisPostCategories.builder()
                .hasNext(hasNext)
                .redisPostsContents(redisPostsContents)
                .build();
    }

    public void saveSlicePostsInRedis(final List<TechBlogPostCategoryDto> contents, final String key,
                                      final boolean hasNext) {
        final List<RedisTechBlogPostsCategoriesStaticData> staticCacheData = RedisTechBlogPostsCategoriesStaticData.from(
                contents, hasNext);

        staticCacheData.forEach((dto) -> {
            double score = -dto.redisTechBlogPostStaticData().writtenAt().toEpochDay();
            redisTemplate.opsForZSet().add(key, dto, score);
        });
        redisTemplate.expire(key, 3600, TimeUnit.SECONDS);

        //opsForHash()를 사용해서 redis에 데이터를 저장함 - 객체를 각 필드 마다 map으로 저장
        final List<RedisPostDynamicData> redisPostDynamicData = RedisPostDynamicData.from(contents);

        redisPostDynamicDataRepository.saveAll(redisPostDynamicData);

    }

    public List<RedisPostsContents> findRecommendPostsByIdsInRedis(final List<Long> postIds) {

        final Iterable<RedisTechBlogPostsCategoriesStaticData> staticDataIterable = redisPostCategoryStaticDataRepository.findAllById(
                postIds);

        final List<RedisTechBlogPostsCategoriesStaticData> staticData = StreamSupport.stream(
                        staticDataIterable.spliterator(),
                        false)
                .toList();

        //정적 정보 TTL 초기화
        staticData.forEach((post) -> {
            redisPostCategoryStaticDataRepository.deleteById(post.postId());
            redisPostCategoryStaticDataRepository.save(post);
        });

        final List<Long> keys = staticData.stream()
                .map(RedisTechBlogPostsCategoriesStaticData::postId)
                .toList();

        final Iterable<RedisPostDynamicData> postDynamicData = redisPostDynamicDataRepository.findAllById(keys);

        final Map<Long, RedisPostDynamicData> postDynamicDataMap = StreamSupport.stream(postDynamicData.spliterator(),
                        false)
                .collect(Collectors.toMap(RedisPostDynamicData::getPostId, Function.identity()));

        //동적 정보 TTL 초기화
        postDynamicDataMap.forEach((keyValue, value) -> {
            redisPostDynamicDataRepository.deleteById(keyValue);
            redisPostDynamicDataRepository.save(value);
        });

        return staticData.stream()
                .map(data -> RedisPostsContents.builder()
                        .redisTechBlogPostDynamicData(postDynamicDataMap.get(data.redisTechBlogPostStaticData().id()))
                        .redisTechBlogPostStaticData(data.redisTechBlogPostStaticData())
                        .redisCategories(data.redisCategories())
                        .build())
                .toList();

    }

    public void saveRecommendPostsInRedis(final List<TechBlogPostCategoryDto> contents) {
        final List<RedisTechBlogPostsCategoriesStaticData> staticCacheData = RedisTechBlogPostsCategoriesStaticData.from(
                contents);
        redisPostCategoryStaticDataRepository.saveAll(staticCacheData);

        final List<RedisPostDynamicData> redisPostDynamicData = RedisPostDynamicData.from(contents);

        redisPostDynamicDataRepository.saveAll(redisPostDynamicData);


    }

    //redisTemplate.delete()를 사용해서 redis에 저장된 데이터를 삭제할 수 있음
    //jitter를 사용해서 redis에 저장된 데이터를 삭제하는 메서드를 만들어보자
    public void deleteKeysWithJitter() throws InterruptedException {
        try (Cursor<byte[]> cursor = redisTemplate.getConnectionFactory().getConnection().scan(ScanOptions.NONE)) {
            while (cursor.hasNext()) {
                final String key = new String(cursor.next());
                final long seconds = applyJitterSeconds();
                redisTemplate.expire(key, seconds, TimeUnit.SECONDS);
            }
        }
    }

    private long applyJitterSeconds() throws InterruptedException {
        // 지터 시간은 예를 들어 100ms에서 1000ms 사이의 랜덤한 시간으로 설정
        return (long) (Math.random() * 900 + 100);
    }

}
