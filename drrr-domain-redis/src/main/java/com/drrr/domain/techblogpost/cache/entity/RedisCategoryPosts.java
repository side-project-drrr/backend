package com.drrr.domain.techblogpost.cache.entity;

import com.drrr.domain.techblogpost.cache.RedisTechBlogPostCategory;
import com.drrr.domain.techblogpost.cache.request.RedisPageRequest;
import java.io.Serializable;
import java.util.List;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash(value = "redisCategoryTechBlogPost")
@Builder
public record RedisCategoryPosts(
        @Id
        CompoundCategoriesPostId id,
        List<RedisTechBlogPostCategory> redisTechBlogPostCategories,
        boolean hasNext
) implements Serializable {

    @Builder
    public record CompoundCategoriesPostId(
            RedisPageRequest redisPageRequest,
            Long categoryId
    ) implements Serializable {
    }

}

