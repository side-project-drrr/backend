package com.drrr.domain.techblogpost.cache.entity;

import com.drrr.core.code.techblog.TopTechBlogType;
import com.drrr.domain.techblogpost.cache.RedisTechBlogPostCategory;
import java.io.Serializable;
import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash(value = "redisTopPostCategories")
@Builder
public record RedisTopPostCategories(
        @Id
        CompoundTopPostCategoriesId id,
        List<RedisTechBlogPostCategory> redisTechBlogPostCategories
) implements Serializable {

    @EqualsAndHashCode
    @Builder
    public static class CompoundTopPostCategoriesId implements Serializable {
        TopTechBlogType topTechBlogType;
        int count;
    }

}
