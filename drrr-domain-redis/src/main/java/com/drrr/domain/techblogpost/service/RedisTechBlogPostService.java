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
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
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
    private final String REDIS_MEMBER_RECOMMENDATION_POST = "recommendation:posts:member:%s";
    private final ObjectMapper objectMapper;

    public <T> Boolean hasCachedKeyByRange(final int page, final int size, final String key, final Long memberId) {
        final int start = page * size;
        final int end = start + size - 1;

        final List<RedisTechBlogPostsCategoriesStaticData> staticData = Objects.requireNonNull(
                        redisTemplate.opsForZSet()
                                .range(key, start, end))
                .stream()
                .filter(Objects::nonNull)
                .map((data) -> (RedisTechBlogPostsCategoriesStaticData) data)
                .toList();

        final Set<Long> postIdSet = staticData.stream()
                .map(RedisTechBlogPostsCategoriesStaticData::postId)
                .collect(Collectors.toSet());

        if (staticData.isEmpty() || postIdSet.size() != staticData.size()) {
            return false;
        }

        final List<Long> postIds = staticData.stream()
                .map(RedisTechBlogPostsCategoriesStaticData::postId)
                .toList();

        redisTemplate.opsForValue()
                .set(key + start + end, staticData, RedisTtlConstants.FIVE_MINUTES.getTtl(), TimeUnit.SECONDS);

        final boolean hasDynamicCacheKey = dynamicDataService.hasDynamicCacheKey(postIds);

        if (!hasDynamicCacheKey) {
            return false;
        }

        return true;

    }

    public RedisPostCategories findCacheSlicePostsInRedis(final int page, final int size, final String key,
                                                          final Long memberId) {
        final int start = page * size;
        final int end = start + size - 1;

        final List<RedisTechBlogPostsCategoriesStaticData> posts = objectMapper.convertValue(
                redisTemplate.opsForValue()
                        .get(key + start + end),
                new TypeReference<>() {
                });

        boolean hasNext = false;

        if (Objects.nonNull(posts)) {
            hasNext = posts.get(posts.size() - 1).hasNext();
        }

        //정적 정보 TTL 초기화
        redisTemplate.expire(key, RedisTtlConstants.FIVE_MINUTES.getTtl(), TimeUnit.SECONDS);

        final List<Long> dynamicPostIds = posts.stream()
                .map((post) -> post.redisTechBlogPostStaticData().id())
                .toList();

        final Set<Long> memberLikedPostIdSet = dynamicDataService.findMemberLikedPostIdSet(memberId);

        final Map<Long, RedisPostDynamicData> postDynamicDataMap = dynamicDataService.findDynamicData(dynamicPostIds);

        final List<RedisSlicePostsContents> redisSlicePostsContents = RedisSlicePostsContents.fromRedisData(posts,
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

        IntStream.range(0, staticCacheData.size())
                .forEach((index) -> {
                    final double score = -(
                            staticCacheData.get(index).redisTechBlogPostStaticData().writtenAt().toEpochDay() - index);
                    redisTemplate.opsForZSet().add(key, staticCacheData.get(index), score);
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
        //특정 사용자에게 추천할 게시물의 id를 가져옴
        final List<Long> postIds =
                Objects.requireNonNull(redisTemplate.opsForZSet().range(String.format(key, memberId), 0, count - 1))
                        .stream()
                        .filter(Objects::nonNull)
                        .map((data) -> (Long) data)
                        .toList();

        if (postIds.size() != count) {
            return false;
        }

        //post id에 해당하는 게시물 정보 가져옴
        final List<RedisPostsCategoriesStaticData> posts = postIds.stream()
                .map(postId -> {
                    redisTemplate.expire("postId:" + postIds, RedisTtlConstants.TEN_MINUTES.getTtl(), TimeUnit.SECONDS);
                    return objectMapper.convertValue(
                            redisTemplate.opsForHash()
                                    .get("postId:" + postId, "redisTechBlogPostStaticData"),
                            RedisPostsCategoriesStaticData.class);
                })
                .filter(Objects::nonNull)
                .toList();

        if (posts.isEmpty()) {
            return false;
        }

        redisTemplate.opsForValue()
                .set(String.format(REDIS_MEMBER_RECOMMENDATION_POST, memberId), posts,
                        RedisTtlConstants.FIVE_MINUTES.getTtl(),
                        TimeUnit.SECONDS);

        final boolean hasDynamicCacheKey = dynamicDataService.hasDynamicCacheKey(postIds);

        if (!hasDynamicCacheKey) {
            return false;
        }

        return true;
    }

    public List<RedisSlicePostsContents> findRedisZSetByKey(final Long memberId, final String key) {

        redisTemplate.expire(String.format(key, memberId), 300, TimeUnit.SECONDS);

        final List<RedisPostsCategoriesStaticData> posts = objectMapper.convertValue(
                redisTemplate.opsForValue()
                        .get(String.format(key, memberId)),
                new TypeReference<>() {
                });

        final List<Long> dynamicPostIds = posts.stream()
                .map((post) -> post.redisTechBlogPostStaticData().id())
                .toList();

        final Set<Long> memberLikedPostIdSet = dynamicDataService.findMemberLikedPostIdSet(memberId);

        final Map<Long, RedisPostDynamicData> postDynamicDataMap = dynamicDataService.findDynamicData(dynamicPostIds);

        dynamicDataService.initiateRedisTtl(postDynamicDataMap, redisTemplate, memberId);

        return RedisSlicePostsContents.from(posts, postDynamicDataMap, memberLikedPostIdSet);
    }

    public void saveRedisRecommendationPost(
            final Long memberId,
            final List<TechBlogPostCategoryDto> contents,
            final List<TechBlogPostLike> memberLikedPosts,
            final String key
    ) {
        final List<RedisPostsCategoriesStaticData> redisPostsCategoriesStaticData = RedisPostsCategoriesStaticData.from(
                contents);

        IntStream.range(0, contents.size())
                .forEach((index) -> {
                    final double score = -(
                            contents.get(index).techBlogPostStaticDataDto().writtenAt().toEpochDay() - index);
                    redisTemplate.opsForZSet()
                            .add(String.format(key, memberId), contents.get(index).techBlogPostStaticDataDto().id(),
                                    score);
                });

        redisTemplate.delete(String.format(REDIS_MEMBER_RECOMMENDATION_POST, memberId));
        redisTemplate.opsForValue()
                .set(String.format(REDIS_MEMBER_RECOMMENDATION_POST, memberId), redisPostsCategoriesStaticData,
                        RedisTtlConstants.FIVE_MINUTES.getTtl(),
                        TimeUnit.SECONDS);
        saveRedisHash(redisPostsCategoriesStaticData);

        final List<Long> memberLikedPostIds = memberLikedPosts.stream()
                .map(like -> like.getPost().getId())
                .toList();

        dynamicDataService.saveDynamicData(RedisPostDynamicData.from(contents));
        dynamicDataService.saveMemberLikedPosts(memberId, memberLikedPostIds);

    }

    private void saveRedisHash(final List<RedisPostsCategoriesStaticData> redisPostsCategoriesStaticData) {

        redisPostsCategoriesStaticData.forEach(data -> {
            redisTemplate.opsForHash().put(
                    "postId:" + data.postId(),
                    "redisTechBlogPostStaticData",
                    data
            );
            redisTemplate.expire("postId:" + data.postId(), 300, TimeUnit.SECONDS);
        });
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

        saveRedisHash(redisPostsCategoriesStaticData);

        final List<Long> memberLikedPostIds = memberLikedPosts.stream()
                .map(like -> like.getPost().getId())
                .toList();

        dynamicDataService.saveDynamicData(RedisPostDynamicData.from(contents));
        dynamicDataService.saveMemberLikedPosts(memberId, memberLikedPostIds);

    }

}
