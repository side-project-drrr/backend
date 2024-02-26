package com.drrr.domain.techblogpost.entity;

import com.drrr.domain.category.entity.RedisCategory;
import java.io.Serializable;
import java.util.List;
import lombok.Builder;

@Builder
public record RedisTechBlogPostCategory(
        RedisTechBlogPostBasicInfo redisTechBlogPostBasicInfo,
        List<RedisCategory> redisCategories
) implements Serializable {
}
