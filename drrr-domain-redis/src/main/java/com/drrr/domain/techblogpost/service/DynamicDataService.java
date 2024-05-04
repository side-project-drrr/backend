package com.drrr.domain.techblogpost.service;

import com.drrr.domain.techblogpost.cache.entity.RedisPostDynamicData;
import com.drrr.domain.techblogpost.constant.RedisMemberConstants;
import com.drrr.domain.techblogpost.constant.RedisTtlConstants;
import com.drrr.domain.techblogpost.repository.RedisPostDynamicDataRepository;
import com.drrr.domain.util.MapperUtils;
import com.fasterxml.jackson.databind.JavaType;
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

@RequiredArgsConstructor
@Service
public class DynamicDataService {
    private final RedisPostDynamicDataRepository redisPostDynamicDataRepository;
    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final String REDIS_MEMBER_POST_DYNAMIC_DATA = "memberId:%s";

    public void initiateRedisTtl(final Map<Long, RedisPostDynamicData> postDynamicDataMap,
                                 final RedisTemplate<String, Object> redisTemplate,
                                 final Long memberId) {
            //동적 정보 TTL 초기화
            postDynamicDataMap.forEach((keyValue, value) -> {
                redisPostDynamicDataRepository.deleteById(keyValue);
                redisPostDynamicDataRepository.save(value);
            });

        if(!memberId.equals(RedisMemberConstants.GUEST.getId())) {
            redisTemplate.expire(String.valueOf(memberId), RedisTtlConstants.TEN_MINUTES.getTtl(), TimeUnit.SECONDS);
        }

    }

    public Set<Long> findMemberLikedPostIdSet(final Long memberId) {
        if(RedisMemberConstants.GUEST.getId().equals(memberId)) {
            return Collections.emptySet();
        }

        return objectMapper.convertValue(
                redisTemplate.opsForSet().members(String.format(REDIS_MEMBER_POST_DYNAMIC_DATA, memberId)),Set.class);

    }
}
