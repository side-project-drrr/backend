package com.drrr.domain.util;

import com.drrr.domain.techblogpost.cache.payload.RedisSlicePostsContents;
import com.drrr.domain.techblogpost.service.RedisTechBlogPostService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisTemplateTestUtil {
    @Autowired
    private RedisTechBlogPostService redisTechBlogPostService;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    private final String REDIS_MEMBER_RECOMMENDATION_POST = "recommendation:posts:member:%s";

    public List<RedisSlicePostsContents> findCacheMemberRecommendation(final Long memberId, final int count) {
        return redisTechBlogPostService.findRedisZSetByKey(memberId, REDIS_MEMBER_RECOMMENDATION_POST);

    }

    public void flushAll() {

        redisTemplate.execute((RedisConnection connection) -> {
            connection.serverCommands().flushAll();
            return "OK";
        });
    }
}
