package com.drrr.domain.techblogpost.service;

import com.drrr.domain.techblogpost.cache.entity.RedisPostDynamicData;
import com.drrr.domain.techblogpost.constant.RedisMemberConstants;
import com.drrr.domain.techblogpost.constant.RedisTtlConstants;
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
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class DynamicDataService {
    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final String REDIS_MEMBER_POST_DYNAMIC_DATA = "memberId:%s";
    private final String REDIS_DYNAMIC_POST_DATA = "redisDynamicPostData:%s";

    public boolean hasDynamicCacheKey(final List<Long> keys) {
        return keys.stream().allMatch((key) -> redisTemplate.hasKey(String.format(REDIS_DYNAMIC_POST_DATA, key)));
    }

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

        redisTemplate.execute(new SessionCallback<List<Object>>() {
            @Override
            public List<Object> execute(RedisOperations operations) throws DataAccessException {
                String key = String.format(REDIS_MEMBER_POST_DYNAMIC_DATA, memberId);
                operations.watch(key);
                operations.multi();
                operations.opsForSet().add(key, postIds.toArray());
                operations.expire(key, RedisTtlConstants.TEN_MINUTES.getTtl(), TimeUnit.SECONDS);
                return operations.exec();
            }
        });
    }

    public void saveDynamicData(final List<RedisPostDynamicData> redisPostDynamicData) {

        redisTemplate.execute(new SessionCallback<List<Object>>() {
            @Override
            public List<Object> execute(RedisOperations operations) throws DataAccessException {
                operations.watch(REDIS_DYNAMIC_POST_DATA);
                operations.multi();
                redisPostDynamicData.forEach((data) -> {
                    String key = String.format(REDIS_DYNAMIC_POST_DATA, data.getPostId());
                    operations.opsForHash().put(key, "viewCount", data.getViewCount());
                    operations.opsForHash().put(key, "likeCount", data.getLikeCount());
                    operations.expire(key, RedisTtlConstants.TEN_MINUTES.getTtl(), TimeUnit.SECONDS);
                });
                return operations.exec();
            }
        });
    }
}
