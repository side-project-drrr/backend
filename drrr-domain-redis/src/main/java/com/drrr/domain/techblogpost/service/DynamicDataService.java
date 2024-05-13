package com.drrr.domain.techblogpost.service;

import com.drrr.domain.techblogpost.cache.entity.RedisPostDynamicData;
import com.drrr.domain.techblogpost.constant.RedisMemberConstants;
import com.drrr.domain.techblogpost.constant.RedisTtlConstants;
import com.drrr.domain.techblogpost.repository.RedisPostDynamicDataRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class DynamicDataService {
    private final RedisPostDynamicDataRepository redisPostDynamicDataRepository;
    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final String REDIS_MEMBER_POST_DYNAMIC_DATA = "memberId:%s";
    private final String REDIS_DYNAMIC_POST_DATA = "redisDynamicPostData:%s";

    public void initiateRedisTtl(final Map<Long, RedisPostDynamicData> postDynamicDataMap,
                                 final RedisTemplate<String, Object> redisTemplate,
                                 final Long memberId) {
        //동적 정보 TTL 초기화
        postDynamicDataMap.forEach((keyValue, value) -> {
            redisTemplate.expire(String.format(REDIS_DYNAMIC_POST_DATA, keyValue),
                    RedisTtlConstants.TEN_MINUTES.getTtl(), TimeUnit.SECONDS);
        });

        if (!memberId.equals(RedisMemberConstants.GUEST.getId())) {
            redisTemplate.expire(String.valueOf(memberId), RedisTtlConstants.TEN_MINUTES.getTtl(), TimeUnit.SECONDS);
        }

    }

    public Set<Long> findMemberLikedPostIdSet(final Long memberId) {
        if (RedisMemberConstants.GUEST.getId().equals(memberId)) {
            return Collections.emptySet();
        }

        return objectMapper.convertValue(
                redisTemplate.opsForSet().members(String.format(REDIS_MEMBER_POST_DYNAMIC_DATA, memberId)), Set.class);

    }

    public Map<Long, RedisPostDynamicData> findDynamicData(final List<Long> postIds) {
        System.out.println("********************* DynamicDataService.findDynamicData*********************");
        System.out.println("postId = " + postIds);
        return postIds.stream().collect(Collectors.toMap(Function.identity(), postId -> {
            final Map<Object, Object> entries = redisTemplate.opsForHash()
                    .entries(String.format(REDIS_DYNAMIC_POST_DATA, postId));

            int viewCount = (int) entries.get("viewCount");
            int likeCount = (int) entries.get("likeCount");

            return RedisPostDynamicData.builder()
                    .postId(postId)
                    .viewCount(viewCount)
                    .likeCount(likeCount)
                    .build();
        }));
    }

    public void saveMemberLikedPosts(final Long memberId, final List<Long> postIds) {
        if (Objects.isNull(postIds) || postIds.isEmpty()) {
            return;
        }

        redisTemplate.opsForSet().add(String.format(REDIS_MEMBER_POST_DYNAMIC_DATA, memberId), postIds.toArray());
        redisTemplate.expire(String.format(REDIS_MEMBER_POST_DYNAMIC_DATA, memberId),
                RedisTtlConstants.TEN_MINUTES.getTtl(), TimeUnit.SECONDS);
    }

    public void saveDynamicData(final List<RedisPostDynamicData> redisPostDynamicData) {
        redisPostDynamicData.forEach((data) -> {
            redisTemplate.opsForHash().put(String.format(REDIS_DYNAMIC_POST_DATA, data.getPostId()), "viewCount",
                    data.getViewCount());
            redisTemplate.opsForHash().put(String.format(REDIS_DYNAMIC_POST_DATA, data.getPostId()), "likeCount",
                    data.getLikeCount());
            redisTemplate.expire(String.format(REDIS_DYNAMIC_POST_DATA, data.getPostId()),
                    RedisTtlConstants.TEN_MINUTES.getTtl(), TimeUnit.SECONDS);
        });
    }
}
