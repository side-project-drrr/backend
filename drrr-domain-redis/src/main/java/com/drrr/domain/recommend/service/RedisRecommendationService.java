package com.drrr.domain.recommend.service;

import com.drrr.domain.recommend.cache.entity.RedisPostsCategoriesStaticData;
import com.drrr.domain.techblogpost.cache.entity.RedisPostDynamicData;
import com.drrr.domain.techblogpost.cache.payload.RedisSlicePostsContents;
import com.drrr.domain.techblogpost.dto.TechBlogPostCategoryDto;
import com.drrr.domain.techblogpost.repository.RedisPostDynamicDataRepository;
import com.drrr.domain.util.MapperUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
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

    public boolean hasCachedKey(final Long memberId) {
        return Boolean.TRUE.equals(redisTemplate.hasKey("recommendation:member:" + memberId));
    }

    public List<RedisSlicePostsContents> findMemberRecommendation(final Long memberId) {
        List<Long> postIds = objectMapper.convertValue(
                redisTemplate.opsForValue().get("recommendation:member:" + memberId),
                mapperUtils.mapType(List.class, Long.class)
        );

        redisTemplate.expire("recommendation:member:" + memberId, 300, TimeUnit.SECONDS);

        List<RedisPostsCategoriesStaticData> recommendation = postIds.stream()
                .map(postId -> {
                    redisTemplate.expire("postId:" + postIds, 300, TimeUnit.SECONDS);
                    return objectMapper.convertValue(
                            redisTemplate.opsForHash()
                                    .get("postId:" + postId, "redisTechBlogPostStaticData"),
                            RedisPostsCategoriesStaticData.class);
                })
                .toList();

        Iterable<RedisPostDynamicData> postDynamicData = redisPostDynamicDataRepository.findAllById(postIds);

        final Map<Long, RedisPostDynamicData> postDynamicDataMap = StreamSupport.stream(postDynamicData.spliterator(),
                        false)
                .collect(Collectors.toMap(RedisPostDynamicData::getPostId, Function.identity()));

        return RedisSlicePostsContents.from(recommendation, postDynamicDataMap);
    }

    public void saveMemberRecommendation(final Long memberId, final List<TechBlogPostCategoryDto> contents) {
        final List<RedisPostDynamicData> redisPostDynamicData = RedisPostDynamicData.from(contents);
        List<RedisPostsCategoriesStaticData> redisPostsCategoriesStaticData = RedisPostsCategoriesStaticData.from(
                contents);

        List<Long> postIds = redisPostDynamicData.stream().map(RedisPostDynamicData::getPostId).toList();

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

        redisPostDynamicDataRepository.saveAll(redisPostDynamicData);

    }
}
