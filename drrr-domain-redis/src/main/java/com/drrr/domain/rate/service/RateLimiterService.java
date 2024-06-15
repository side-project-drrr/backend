package com.drrr.domain.rate.service;

import java.time.Duration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RateLimiterService {

    private final RedisTemplate<String, String> redisTemplate;

    public RateLimiterService(final RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean isAllowed(final String clientIp, final String api, final int limit, final Duration duration) {
        final String key = String.format("ratelimit:%s:%s", clientIp, api);

        final long current = redisTemplate.opsForHash().increment(key, api, 1);
        if (current == 1L) {
            redisTemplate.expire(key, duration);
        }

        return current <= limit;
    }
}
