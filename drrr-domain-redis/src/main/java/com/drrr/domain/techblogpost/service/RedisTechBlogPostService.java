package com.drrr.domain.techblogpost.service;

import com.drrr.domain.like.entity.TechBlogPostLike;
import com.drrr.domain.recommend.cache.entity.RedisPostsCategoriesStaticData;
import com.drrr.domain.techblogpost.cache.entity.RedisPostCategories;
import com.drrr.domain.techblogpost.cache.entity.RedisPostDynamicData;
import com.drrr.domain.techblogpost.cache.entity.RedisTechBlogPostsCategoriesStaticData;
import com.drrr.domain.techblogpost.cache.payload.RedisSlicePostsContents;
import com.drrr.domain.techblogpost.constant.RedisMemberConstants;
import com.drrr.domain.techblogpost.constant.RedisTtlConstants;
import com.drrr.domain.techblogpost.dto.TechBlogPostCategoryDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class RedisTechBlogPostService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final DynamicDataService dynamicDataService;
    private final String REDIS_MEMBER_POST_DYNAMIC_DATA = "memberId:%s";
    private final ObjectMapper objectMapper;

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


    public RedisPostCategories findCacheSlicePostsInRedis(final int page, final int size, final String key,
                                                          final Long memberId) {
        final int start = page * size;
        final int end = start + size - 1;

        final List<RedisTechBlogPostsCategoriesStaticData> staticData = Objects.requireNonNull(
                        redisTemplate.opsForZSet()
                                .range(key, start, end))
                .stream()
                .filter(Objects::nonNull)
                .map((data) -> (RedisTechBlogPostsCategoriesStaticData) data)
                .toList();
        System.out.println("SIZE staticData 사이즈 -> " + staticData.size());
        for (RedisTechBlogPostsCategoriesStaticData staticDatum : staticData) {
            System.out.println("staticDatum -> " + staticDatum.postId());
        }

        boolean hasNext = false;

        if (!staticData.isEmpty()) {
            hasNext = staticData.get(staticData.size() - 1).hasNext();
        }

        //정적 정보 TTL 초기화
        redisTemplate.expire(key, RedisTtlConstants.FIVE_MINUTES.getTtl(), TimeUnit.SECONDS);

        final List<Long> keys = staticData.stream()
                .map((post) -> post.redisTechBlogPostStaticData().id())
                .toList();

        System.out.println("##################################################");
        System.out.println("##################################################");
        System.out.println("##################################################");
        System.out.println("모든 게시물 이나 카테고리에 해당하는 게시물 찾는 API 의 Keys 리스트");
        System.out.println("keys -> " + keys);

        final Set<Long> memberLikedPostIdSet = dynamicDataService.findMemberLikedPostIdSet(memberId);

        final Map<Long, RedisPostDynamicData> postDynamicDataMap = dynamicDataService.findDynamicData(keys);

        final List<RedisSlicePostsContents> redisSlicePostsContents = RedisSlicePostsContents.fromRedisData(staticData,
                postDynamicDataMap, memberLikedPostIdSet);

        //동적 정보 TTL 초기화
        dynamicDataService.initiateRedisTtl(postDynamicDataMap, redisTemplate, memberId);

        return RedisPostCategories.builder()
                .hasNext(hasNext)
                .redisSlicePostsContents(redisSlicePostsContents)
                .build();
    }

    public void saveSlicePostsInRedis(final List<TechBlogPostCategoryDto> contents,
                                      final String key,
                                      final boolean hasNext,
                                      final Set<Long> memberLikedPostIdSet,
                                      final Long memberId) {
        final List<RedisTechBlogPostsCategoriesStaticData> staticCacheData = RedisTechBlogPostsCategoriesStaticData.from(
                contents, hasNext);

        //opsForZSet()를 사용해서 redis에 데이터를 저장함 - 객체를 score로 저장

        staticCacheData.forEach((dto) -> {
            final double score = -dto.redisTechBlogPostStaticData().writtenAt().toEpochDay();
            redisTemplate.opsForZSet().add(key, dto, score);
        });

        //사용자 좋아요 여부 정보 TTL 초기화 및 저장
        if (!memberId.equals(RedisMemberConstants.GUEST.getId())) {
            memberLikedPostIdSet
                    .forEach((postId) -> redisTemplate.opsForSet()
                            .add(String.format(REDIS_MEMBER_POST_DYNAMIC_DATA, memberId), postId));
        }

        redisTemplate.expire(String.format(REDIS_MEMBER_POST_DYNAMIC_DATA, memberId),
                RedisTtlConstants.TEN_MINUTES.getTtl(), TimeUnit.SECONDS);

        //게시물의 좋아요 및 조회수 정보 저장
        dynamicDataService.saveDynamicData(RedisPostDynamicData.from(contents));

    }

    public boolean hasCachedKey(final Long memberId, final int count, final String key) {
        final Set<Object> recommendationPostIdSet = redisTemplate.opsForZSet()
                .range(String.format(key, memberId), 0, -1);

        return recommendationPostIdSet.size() == count;
    }

    public List<RedisSlicePostsContents> findRedisZSetByKey(final Long memberId, final int count, final String key) {
        final List<Long> postIds = Objects.requireNonNull(
                        redisTemplate.opsForZSet().range(String.format(key, memberId), 0, count - 1))
                .stream()
                .filter(Objects::nonNull)
                .map((data) -> (Long) data)
                .toList();

        redisTemplate.expire(String.format(key, memberId), 300, TimeUnit.SECONDS);

        final List<RedisPostsCategoriesStaticData> recommendation = postIds.stream()
                .map(postId -> {
                    redisTemplate.expire("postId:" + postIds, RedisTtlConstants.TEN_MINUTES.getTtl(), TimeUnit.SECONDS);
                    return objectMapper.convertValue(
                            redisTemplate.opsForHash()
                                    .get("postId:" + postId, "redisTechBlogPostStaticData"),
                            RedisPostsCategoriesStaticData.class);
                })
                .toList();

        final Set<Long> memberLikedPostIdSet = dynamicDataService.findMemberLikedPostIdSet(memberId);

        final Map<Long, RedisPostDynamicData> postDynamicDataMap = dynamicDataService.findDynamicData(postIds);

        dynamicDataService.initiateRedisTtl(postDynamicDataMap, redisTemplate, memberId);

        return RedisSlicePostsContents.from(recommendation, postDynamicDataMap, memberLikedPostIdSet);
    }

    public void saveRedisZSetByKey(
            final Long memberId,
            final List<TechBlogPostCategoryDto> contents,
            final List<TechBlogPostLike> memberLikedPosts,
            final String key
    ) {
        final List<RedisPostsCategoriesStaticData> redisPostsCategoriesStaticData = RedisPostsCategoriesStaticData.from(
                contents);

        memberLikedPosts.forEach(content -> {
            double score = 0;
            if (key.contains("LIKES")) {
                score = content.getPost().getPostLike();
            } else {
                score = content.getPost().getViewCount();
            }

            redisTemplate.opsForZSet()
                    .add(key, content.getId(),
                            score);
        });

        redisPostsCategoriesStaticData.forEach(data -> {
            redisTemplate.opsForHash().put(
                    "postId:" + data.postId(),
                    "redisTechBlogPostStaticData",
                    data
            );
            redisTemplate.expire("postId:" + data.postId(), 300, TimeUnit.SECONDS);
        });

        final List<Long> memberLikedPostIds = memberLikedPosts.stream()
                .map(like -> like.getPost().getId())
                .toList();

        dynamicDataService.saveDynamicData(RedisPostDynamicData.from(contents));
        dynamicDataService.saveMemberLikedPosts(memberId, memberLikedPostIds);

    }

}
