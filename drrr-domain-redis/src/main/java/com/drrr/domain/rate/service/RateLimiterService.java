package com.drrr.domain.rate.service;

import java.time.Duration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

@Service
public class RateLimiterService {

    private final RedisTemplate<String, String> redisTemplate;

    public RateLimiterService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean isAllowed(String clientIp, String api, int limit, Duration duration) {
        String key = String.format("ratelimit:%s:%s", clientIp, api);
        ValueOperations<String, String> ops = redisTemplate.opsForValue();

        Long current = ops.increment(key, 1);
        if (current == 1) {
            redisTemplate.expire(key, duration);
        }

        return current <= limit;
    }
}
