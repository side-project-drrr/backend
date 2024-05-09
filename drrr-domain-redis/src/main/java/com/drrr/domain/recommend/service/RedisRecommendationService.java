package com.drrr.domain.recommend.service;

import com.drrr.domain.like.entity.TechBlogPostLike;
import com.drrr.domain.recommend.cache.entity.RedisPostsCategoriesStaticData;
import com.drrr.domain.techblogpost.cache.entity.RedisPostDynamicData;
import com.drrr.domain.techblogpost.cache.payload.RedisSlicePostsContents;
import com.drrr.domain.techblogpost.constant.RedisTtlConstants;
import com.drrr.domain.techblogpost.dto.TechBlogPostCategoryDto;
import com.drrr.domain.techblogpost.repository.RedisPostDynamicDataRepository;
import com.drrr.domain.techblogpost.service.DynamicDataService;
import com.drrr.domain.util.MapperUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class RedisRecommendationService {

    private final ObjectMapper objectMapper;
    private final MapperUtils mapperUtils;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisPostDynamicDataRepository redisPostDynamicDataRepository;
    private final DynamicDataService dynamicDataService;
    private final String RECOMMENDATION_MEMBER = "recommendation:member:%s";

    public boolean hasCachedKey(final Long memberId, final int count) {
        final Set<Object> recommendationPostIdSet = redisTemplate.opsForZSet().range(String.format(RECOMMENDATION_MEMBER, memberId), 0, -1);

        return recommendationPostIdSet.size() == count;
    }

    public List<RedisSlicePostsContents> findMemberRecommendation(final Long memberId) {
        final Set<Long> postIds = objectMapper.convertValue(
                redisTemplate.opsForZSet().range(String.format(RECOMMENDATION_MEMBER,memberId) , 0, -1),
                mapperUtils.mapType(Set.class, Long.class)
        );

        redisTemplate.expire(String.format(RECOMMENDATION_MEMBER,memberId), 300, TimeUnit.SECONDS);

        final List<RedisPostsCategoriesStaticData> recommendation = postIds.stream()
                .map(postId -> {
                    redisTemplate.expire("postId:" + postIds, RedisTtlConstants.TEN_MINUTES.getTtl(), TimeUnit.SECONDS);
                    return objectMapper.convertValue(
                            redisTemplate.opsForHash()
                                    .get("postId:" + postId, "redisTechBlogPostStaticData"),
                            RedisPostsCategoriesStaticData.class);
                })
                .toList();

        final Iterable<RedisPostDynamicData> postDynamicData = redisPostDynamicDataRepository.findAllById(postIds);

        final Set<Long> memberLikedPostIdSet = dynamicDataService.findMemberLikedPostIdSet(memberId);

        final Map<Long, RedisPostDynamicData> postDynamicDataMap = RedisPostDynamicData.iterableToMap(postDynamicData);

        dynamicDataService.initiateRedisTtl(postDynamicDataMap, redisTemplate,memberId);

        return RedisSlicePostsContents.from(recommendation, postDynamicDataMap, memberLikedPostIdSet);
    }

    public void saveMemberRecommendation(final Long memberId, final List<TechBlogPostCategoryDto> contents, final List<TechBlogPostLike> memberLikedPosts) {
        final List<RedisPostsCategoriesStaticData> redisPostsCategoriesStaticData = RedisPostsCategoriesStaticData.from(
                contents);


        contents.forEach(content->{
                    double score = -content.techBlogPostStaticDataDto().writtenAt().toEpochDay();
                    redisTemplate.opsForZSet().add(String.format(RECOMMENDATION_MEMBER, memberId), content.techBlogPostStaticDataDto().id(), score);
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
