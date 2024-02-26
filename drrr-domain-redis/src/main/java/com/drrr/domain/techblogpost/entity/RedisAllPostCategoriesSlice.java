package com.drrr.domain.techblogpost.entity;

import java.io.Serializable;
import java.util.List;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash(value = "redisPostCategoriesSlice", timeToLive = 3600) // Redis Repository 사용을 위한
@Builder
public record RedisAllPostCategoriesSlice(
        @Id
        CompoundPostCategoriesSliceId id,
        List<RedisTechBlogPostCategory> redisTechBlogPostCategories,
        boolean hasNext
) implements Serializable {

    @Builder
    public static class CompoundPostCategoriesSliceId implements Serializable {
        RedisPageRequest redisPageRequest;
    }
}
