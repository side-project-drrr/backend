package com.drrr.domain.techblogpost.entity;

import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@RedisHash(value = "redisCategoryTechBlogPost", timeToLive = 3600) // Redis Repository 사용을 위한
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class RedisCategoryTechBlogPost {
    @Id
    private Long id;
    private List<TechBlogPost> techBlogPost;
}
