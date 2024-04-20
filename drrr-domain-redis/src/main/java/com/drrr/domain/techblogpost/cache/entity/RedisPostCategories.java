package com.drrr.domain.techblogpost.cache.entity;

import com.drrr.domain.techblogpost.cache.payload.RedisPostsContents;
import java.util.List;
import lombok.Builder;

@Builder
public record RedisPostCategories(
        List<RedisPostsContents> redisPostsContents,

        boolean hasNext
) {

}
