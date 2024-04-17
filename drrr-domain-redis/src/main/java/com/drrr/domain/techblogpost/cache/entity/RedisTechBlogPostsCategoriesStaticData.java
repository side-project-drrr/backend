package com.drrr.domain.techblogpost.cache.entity;

import com.drrr.domain.category.cache.RedisCategory;
import com.drrr.domain.techblogpost.dto.TechBlogPostCategoryDto;
import java.io.Serializable;
import java.util.List;
import lombok.Builder;

@Builder
public record RedisTechBlogPostsCategoriesStaticData(
        Long postId,
        RedisTechBlogPostStaticData redisTechBlogPostStaticData,
        List<RedisCategory> redisCategories,
        boolean hasNext
) implements Serializable {

    public static List<RedisTechBlogPostsCategoriesStaticData> from(final List<TechBlogPostCategoryDto> contents) {
        return contents.stream()
                .map((content) -> {
                    RedisTechBlogPostStaticData redisTechBlogPostStaticData = RedisTechBlogPostStaticData
                            .from(content.techBlogPostStaticDataDto());

                    List<RedisCategory> redisCategories = RedisCategory.from(content.categoryDto());

                    return RedisTechBlogPostsCategoriesStaticData.builder()
                            .postId(content.techBlogPostStaticDataDto().id())
                            .redisTechBlogPostStaticData(redisTechBlogPostStaticData)
                            .redisCategories(redisCategories)
                            .build();
                })
                .toList();
    }

    public static List<RedisTechBlogPostsCategoriesStaticData> from(final List<TechBlogPostCategoryDto> contents,
                                                                    final boolean hasNext) {
        return contents.stream()
                .map((content) -> {
                    RedisTechBlogPostStaticData redisTechBlogPostStaticData = RedisTechBlogPostStaticData
                            .from(content.techBlogPostStaticDataDto());

                    List<RedisCategory> redisCategories = RedisCategory.from(content.categoryDto());

                    return RedisTechBlogPostsCategoriesStaticData.builder()
                            .postId(content.techBlogPostStaticDataDto().id())
                            .redisTechBlogPostStaticData(redisTechBlogPostStaticData)
                            .redisCategories(redisCategories)
                            .hasNext(hasNext)
                            .build();
                })
                .toList();
    }
}
