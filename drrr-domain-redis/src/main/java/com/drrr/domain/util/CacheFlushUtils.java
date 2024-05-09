package com.drrr.domain.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CacheFlushUtils {
    private final RedisTemplate<String, Object> redisTemplate;
    public void flushAll() {
        redisTemplate.execute((RedisConnection connection) -> {
            connection.serverCommands().flushAll();
            return "OK";
        });
    }
}
