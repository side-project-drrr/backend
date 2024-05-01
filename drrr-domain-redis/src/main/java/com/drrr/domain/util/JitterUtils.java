package com.drrr.domain.util;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class JitterUtils {
    private final RedisTemplate<String, Object> redisTemplate;

    //redisTemplate.delete()를 사용해서 redis에 저장된 데이터를 삭제할 수 있음
    //jitter를 사용해서 redis에 저장된 데이터를 삭제하는 메서드를 만들어보자
    public void deleteKeysWithJitter() throws InterruptedException {
        try (Cursor<String> cursor = redisTemplate.scan(ScanOptions.scanOptions().match("*").count(100).build())) {
            while (cursor.hasNext()) {
                final String key = cursor.next();
                final long seconds = applyJitterSeconds();
                redisTemplate.expire(key, seconds, TimeUnit.SECONDS);
            }
        }
    }

    private long applyJitterSeconds() {
        return (long) (Math.random() * 900 + 100);
    }
}
