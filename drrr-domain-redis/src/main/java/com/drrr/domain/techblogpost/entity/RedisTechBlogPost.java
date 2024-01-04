package com.drrr.domain.techblogpost.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@RedisHash(value = "redisTechBlogPost", timeToLive = 3600) // Redis Repository 사용을 위한
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class RedisTechBlogPost {
    @Id
    private Long id;
    private TechBlogPost techBlogPost;
}
