package com.drrr.domain.recommend.service;

import com.drrr.domain.like.entity.TechBlogPostLike;
import com.drrr.domain.recommend.cache.entity.RedisPostsCategoriesStaticData;
import com.drrr.domain.techblogpost.cache.entity.RedisMemberPostDynamicData;
import com.drrr.domain.techblogpost.cache.entity.RedisPostDynamicData;
import com.drrr.domain.techblogpost.cache.payload.RedisSlicePostsContents;
import com.drrr.domain.techblogpost.dto.TechBlogPostCategoryDto;
import com.drrr.domain.techblogpost.repository.RedisPostDynamicDataRepository;
import com.drrr.domain.techblogpost.service.DynamicDataService;
import com.drrr.domain.util.MapperUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
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
public class RedisRecommendationService {

    private final ObjectMapper objectMapper;
    private final MapperUtils mapperUtils;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisPostDynamicDataRepository redisPostDynamicDataRepository;
    private final DynamicDataService dynamicDataService;

    public boolean hasCachedKey(final Long memberId) {
        return Boolean.TRUE.equals(redisTemplate.hasKey("recommendation:member:" + memberId));
    }

    public List<RedisSlicePostsContents> findMemberRecommendation(final Long memberId) {
        final List<Long> postIds = objectMapper.convertValue(
                redisTemplate.opsForValue().get("recommendation:member:" + memberId),
                mapperUtils.mapType(List.class, Long.class)
        );

        redisTemplate.expire("recommendation:member:" + memberId, 300, TimeUnit.SECONDS);

        final List<RedisPostsCategoriesStaticData> recommendation = postIds.stream()
                .map(postId -> {
                    redisTemplate.expire("postId:" + postIds, 300, TimeUnit.SECONDS);
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
        //동적 정보 저장
        final List<RedisPostDynamicData> redisPostDynamicData = RedisPostDynamicData.from(contents);
        redisPostDynamicDataRepository.saveAll(redisPostDynamicData);


        final List<RedisPostsCategoriesStaticData> redisPostsCategoriesStaticData = RedisPostsCategoriesStaticData.from(
                contents);


        final List<Long> postIds = contents.stream().map(content -> content.techBlogPostStaticDataDto().id()).toList();

        redisTemplate.opsForValue().set("recommendation:member:" + memberId, postIds);
        redisTemplate.expire("recommendation:member:" + memberId, 300, TimeUnit.SECONDS);

        redisPostsCategoriesStaticData.forEach(data -> {
            redisTemplate.opsForHash().put(
                    "postId:" + data.postId(),
                    "redisTechBlogPostStaticData",
                    data
            );
            redisTemplate.expire("postId:" + data.postId(), 300, TimeUnit.SECONDS);
        });

    }
}
