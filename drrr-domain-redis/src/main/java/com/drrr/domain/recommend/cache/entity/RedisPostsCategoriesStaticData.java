package com.drrr.domain.recommend.cache.entity;


import com.drrr.domain.category.cache.RedisCategory;
import com.drrr.domain.techblogpost.cache.entity.RedisTechBlogPostStaticData;
import com.drrr.domain.techblogpost.dto.TechBlogPostCategoryDto;
import java.io.Serializable;
import java.util.List;
import lombok.Builder;

@Builder
public record RedisPostsCategoriesStaticData(
        Long postId,
        RedisTechBlogPostStaticData redisTechBlogPostStaticData,
        List<RedisCategory> redisCategories
) implements Serializable {
    public static List<RedisPostsCategoriesStaticData> from(final List<TechBlogPostCategoryDto> contents) {

        return contents.stream()
                .map((content) -> {
                    RedisTechBlogPostStaticData redisTechBlogPostStaticData = RedisTechBlogPostStaticData
                            .from(content.techBlogPostStaticDataDto());

                    List<RedisCategory> redisCategories = RedisCategory.from(content.categoryDto());

                    return RedisPostsCategoriesStaticData.builder()
                            .postId(redisTechBlogPostStaticData.id())
                            .redisTechBlogPostStaticData(redisTechBlogPostStaticData)
                            .redisCategories(redisCategories)
                            .build();
                })
                .toList();
    }
}
