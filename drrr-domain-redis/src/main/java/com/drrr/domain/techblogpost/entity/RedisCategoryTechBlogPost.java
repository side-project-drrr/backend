package com.drrr.domain.techblogpost.entity;

import java.util.List;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash(value = "redisCategoryTechBlogPost", timeToLive = 3600) // Redis Repository 사용을 위한
@Builder
public record RedisCategoryTechBlogPost(
        @Id
        Long id,
        List<TechBlogPost> techBlogPost
) {
}

