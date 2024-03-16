package com.drrr.domain.techblogpost.entity;

import com.drrr.core.code.techblog.TopTechBlogType;
import com.drrr.domain.techblogpost.cache.RedisTechBlogPostCategory;
import java.io.Serializable;
import java.util.List;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash(value = "redisTopPostCategories")
@Builder
public record RedisTopPostCategories(
        @Id
        CompoundTopPostCategoriesId id,
        List<RedisTechBlogPostCategory> redisTechBlogPostCategories
) implements Serializable {

    @Builder
    public record CompoundTopPostCategoriesId(
            TopTechBlogType topTechBlogType,
            int count
    ) implements Serializable {

    }

}
