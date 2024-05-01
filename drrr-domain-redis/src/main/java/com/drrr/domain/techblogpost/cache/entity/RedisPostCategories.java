package com.drrr.domain.techblogpost.cache.entity;

import com.drrr.domain.techblogpost.cache.payload.RedisSlicePostsContents;
import java.util.List;
import lombok.Builder;

@Builder
public record RedisPostCategories(
        List<RedisSlicePostsContents> redisSlicePostsContents,

        boolean hasNext
) {

}
