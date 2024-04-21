package com.drrr.domain.recommend.payload;

import com.drrr.domain.recommend.cache.entity.RedisPostsCategoriesStaticData;
import com.drrr.domain.techblogpost.cache.entity.RedisPostDynamicData;
import java.util.List;
import lombok.Builder;

@Builder
public record RedisPostsContents(
        RedisPostsCategoriesStaticData redisPostsCategoriesStaticData,
        List<RedisPostDynamicData> redisPostDynamicData
) {
}
