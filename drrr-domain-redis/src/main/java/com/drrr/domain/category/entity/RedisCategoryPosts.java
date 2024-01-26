package com.drrr.domain.category.entity;

import com.drrr.domain.techblogpost.entity.TechBlogPost;
import java.time.LocalDate;
import java.util.Set;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@RedisHash(value = "redisCategoryPosts", timeToLive = 3600) // Redis Repository 사용을 위한
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
@Builder
public class RedisCategoryPosts {
    @Id
    private CompoundCategoryId id;
    private Set<TechBlogPost> posts;

    @ToString
    @EqualsAndHashCode
    public static class CompoundCategoryId {
        private Long categoryId;
        private LocalDate createdDate;
    }

}
