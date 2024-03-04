package com.drrr.domain.techblogpost.entity;

import com.drrr.domain.category.dto.CategoryDto;
import com.drrr.domain.techblogpost.dto.TechBlogPostBasicInfoDto;
import com.drrr.domain.techblogpost.dto.TechBlogPostCategoryDto;
import java.io.Serializable;
import java.util.List;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash(value = "redisPostCategoriesSlice", timeToLive = 3600) // Redis Repository 사용을 위한
@Builder
public record RedisAllPostCategoriesSlice(
        @Id
        CompoundPostCategoriesSliceId id,
        List<RedisTechBlogPostCategory> redisTechBlogPostCategories,
        boolean hasNext
) implements Serializable {

    @Builder
    public static class CompoundPostCategoriesSliceId implements Serializable {
        RedisPageRequest redisPageRequest;
    }

    public static List<TechBlogPostCategoryDto> from(final RedisAllPostCategoriesSlice value) {
        return value.redisTechBlogPostCategories().stream()
                .map((redisEntity) -> TechBlogPostCategoryDto.builder()
                        .techBlogPostBasicInfoDto(TechBlogPostBasicInfoDto.builder()
                                .id(redisEntity.redisTechBlogPostBasicInfo().id())
                                .postLike(redisEntity.redisTechBlogPostBasicInfo().postLike())
                                .summary(redisEntity.redisTechBlogPostBasicInfo().summary())
                                .thumbnailUrl(redisEntity.redisTechBlogPostBasicInfo().thumbnailUrl())
                                .title(redisEntity.redisTechBlogPostBasicInfo().title())
                                .url(redisEntity.redisTechBlogPostBasicInfo().url())
                                .viewCount(redisEntity.redisTechBlogPostBasicInfo().viewCount())
                                .techBlogCode(redisEntity.redisTechBlogPostBasicInfo().techBlogCode())
                                .writtenAt(redisEntity.redisTechBlogPostBasicInfo().writtenAt())
                                .build())
                        .categoryDto(redisEntity.redisCategories().stream()
                                .map(redisCategory -> CategoryDto.builder()
                                        .id(redisCategory.id())
                                        .name(redisCategory.name())
                                        .build())
                                .toList()).build()).toList();
    }


}
