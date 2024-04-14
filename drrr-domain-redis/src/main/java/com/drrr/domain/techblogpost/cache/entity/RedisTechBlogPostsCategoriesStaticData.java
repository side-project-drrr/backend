package com.drrr.domain.techblogpost.cache.entity;

import com.drrr.domain.category.entity.RedisCategory;
import com.drrr.domain.techblogpost.dto.TechBlogPostCategoryDto;
import java.io.Serializable;
import java.util.List;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash(value = "redisTechBlogPostsCategoriesStaticData", timeToLive = 3600) // Redis Repository 사용을 위한
@Builder
public record RedisTechBlogPostsCategoriesStaticData(
        @Id
        Long postId,
        RedisTechBlogPostStaticData redisTechBlogPostStaticData,
        List<RedisCategory> redisCategories
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
}
