package com.drrr.domain.techblogpost.entity;

import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash(value = "redisTechBlogPost", timeToLive = 3600) // Redis Repository 사용을 위한
@Builder
public record RedisTechBlogPost(
        @Id
        Long id,
        TechBlogPost techBlogPost
) {
}

