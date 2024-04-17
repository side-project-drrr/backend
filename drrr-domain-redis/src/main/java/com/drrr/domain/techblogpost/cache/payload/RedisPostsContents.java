package com.drrr.domain.techblogpost.cache.payload;

import com.drrr.domain.category.cache.RedisCategory;
import com.drrr.domain.techblogpost.cache.entity.RedisPostDynamicData;
import com.drrr.domain.techblogpost.cache.entity.RedisTechBlogPostStaticData;
import java.util.List;
import lombok.Builder;

@Builder
public record RedisPostsContents(
        RedisTechBlogPostStaticData redisTechBlogPostStaticData,
        RedisPostDynamicData redisTechBlogPostDynamicData,
        List<RedisCategory> redisCategories
) {
    
}
