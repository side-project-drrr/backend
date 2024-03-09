package com.drrr.domain.techblogpost.cache.entity;

import com.drrr.domain.techblogpost.cache.RedisTechBlogPostCategory;
import com.drrr.domain.techblogpost.cache.request.RedisPageRequest;
import java.io.Serializable;
import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash(value = "redisPostCategoriesSlice")
@Builder
public record RedisPostCategories(
        @Id
        CompoundPostCategoriesId id,
        List<RedisTechBlogPostCategory> redisTechBlogPostCategories,
        boolean hasNext
) implements Serializable {

    @EqualsAndHashCode
    @Builder
    public static class CompoundPostCategoriesId implements Serializable {
        RedisPageRequest redisPageRequest;
    }

}
