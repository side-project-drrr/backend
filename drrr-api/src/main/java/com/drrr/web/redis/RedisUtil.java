package com.drrr.web.redis;

import com.drrr.domain.category.dto.CategoryDto;
import com.drrr.domain.techblogpost.cache.RedisTechBlogPostCategory;
import com.drrr.domain.techblogpost.dto.TechBlogPostBasicInfoDto;
import com.drrr.domain.techblogpost.dto.TechBlogPostCategoryDto;
import java.util.List;

public record RedisUtil() {
    public static List<TechBlogPostCategoryDto> redisPostCategoriesEntityToDto(
            final List<RedisTechBlogPostCategory> redisTechBlogPostCategories
    ) {
        return redisTechBlogPostCategories.stream()
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
