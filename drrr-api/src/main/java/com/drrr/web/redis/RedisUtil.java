package com.drrr.web.redis;

import com.drrr.domain.category.dto.CategoryDto;
import com.drrr.domain.techblogpost.cache.payload.RedisPostsContents;
import com.drrr.domain.techblogpost.dto.TechBlogPostCategoryDto;
import com.drrr.domain.techblogpost.dto.TechBlogPostDynamicDto;
import com.drrr.domain.techblogpost.dto.TechBlogPostStaticDataDto;
import java.util.List;

public record RedisUtil() {
    public static List<TechBlogPostCategoryDto> redisPostCategoriesEntityToDto(
            final List<RedisPostsContents> redisPostsContents
    ) {
        return redisPostsContents.stream()
                .map((redisEntity) -> TechBlogPostCategoryDto.builder()
                        .techBlogPostStaticDataDto(TechBlogPostStaticDataDto.builder()
                                .id(redisEntity.redisTechBlogPostStaticData().id())
                                .summary(redisEntity.redisTechBlogPostStaticData().summary())
                                .thumbnailUrl(redisEntity.redisTechBlogPostStaticData().thumbnailUrl())
                                .title(redisEntity.redisTechBlogPostStaticData().title())
                                .url(redisEntity.redisTechBlogPostStaticData().url())
                                .techBlogCode(redisEntity.redisTechBlogPostStaticData().techBlogCode())
                                .writtenAt(redisEntity.redisTechBlogPostStaticData().writtenAt())
                                .build())
                        .techBlogPostDynamicDto(TechBlogPostDynamicDto.builder()
                                .viewCount(redisEntity.redisTechBlogPostDynamicData().getViewCount())
                                .likeCount(redisEntity.redisTechBlogPostDynamicData().getLikeCount())
                                .build())
                        .categoryDto(redisEntity.redisCategories().stream()
                                .map(redisCategory -> CategoryDto.builder()
                                        .id(redisCategory.id())
                                        .name(redisCategory.name())
                                        .build())
                                .toList()).build()).toList();
    }
}
