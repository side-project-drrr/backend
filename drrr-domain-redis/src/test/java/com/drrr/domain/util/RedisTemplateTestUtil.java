package com.drrr.domain.util;

import com.drrr.domain.recommend.service.RedisRecommendationService;
import com.drrr.domain.techblogpost.cache.payload.RedisSlicePostsContents;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisTemplateTestUtil {
    @Autowired
    private RedisRecommendationService redisRecommendationService;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public List<RedisSlicePostsContents> findCacheMemberRecommendation(final Long memberId, final int count) {
        return redisRecommendationService.findMemberRecommendation(memberId, count);
    }

    public void flushAll() {
        redisTemplate.execute((RedisConnection connection) -> {
            connection.serverCommands().flushAll();
            return "OK";
        });
    }
}
