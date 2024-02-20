package com.drrr.domain.techblogpost.entity;

import com.drrr.domain.techblogpost.dto.TechBlogPostCategoryDto;
import java.io.Serializable;
import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@RedisHash(value = "redisPostCategoriesSlice", timeToLive = 3600) // Redis Repository 사용을 위한
@EqualsAndHashCode
@Builder
public class RedisAllPostCategoriesSlice implements Serializable {
    @Id
    private CompoundPostCategoriesSliceId id;
    private List<TechBlogPostCategoryDto> sliceData;
    private boolean hasNext;

    @Getter
    @ToString
    @Builder
    @EqualsAndHashCode
    public static class CompoundPostCategoriesSliceId implements Serializable {
        private int page;
        private int size;
    }
}
