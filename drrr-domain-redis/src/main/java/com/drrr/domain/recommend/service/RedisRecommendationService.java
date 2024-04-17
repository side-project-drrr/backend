package com.drrr.domain.recommend.service;

import com.drrr.domain.recommend.cache.entity.RedisPostsCategoriesStaticData;
import com.drrr.domain.techblogpost.cache.entity.RedisPostDynamicData;
import com.drrr.domain.techblogpost.cache.payload.RedisSlicePostsContents;
import com.drrr.domain.techblogpost.dto.TechBlogPostCategoryDto;
import com.drrr.domain.techblogpost.repository.RedisPostDynamicDataRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
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
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisPostDynamicDataRepository redisPostDynamicDataRepository;

    public boolean hasCachedKey(final Long memberId) {
        return Boolean.TRUE.equals(redisTemplate.hasKey("recommendation:member:" + memberId));
    }

    public List<RedisSlicePostsContents> findMemberRecommendation(final Long memberId) {
        List<Long> postIds = (List<Long>) redisTemplate.opsForValue().get("recommendation:member:" + memberId);

        List<RedisPostsCategoriesStaticData> recommendation = postIds.stream()
                .map(postId -> {
                    Map<String, Object> map = (Map<String, Object>) redisTemplate.opsForHash()
                            .get("postId:" + postId, "redisTechBlogPostStaticData");
                    return objectMapper.convertValue(map, RedisPostsCategoriesStaticData.class);
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

        redisPostsCategoriesStaticData.forEach(data -> {
            redisTemplate.opsForHash().put(
                    "postId:" + data.postId(),
                    "redisTechBlogPostStaticData",
                    data
            );
        });
        redisPostDynamicDataRepository.saveAll(redisPostDynamicData);

    }
}
