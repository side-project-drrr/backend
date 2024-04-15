package com.drrr.domain.post;

import com.drrr.domain.techblogpost.cache.entity.RedisPostDynamicData;

public class RedisPostDynamicDataFixture {
    public static RedisPostDynamicData createRedisPostDynamicData(final int likes, final int views, final Long postId) {
        return RedisPostDynamicData.builder()
                .postId(postId)
                .likeCount(likes)
                .viewCount(views)
                .build();
    }
}
