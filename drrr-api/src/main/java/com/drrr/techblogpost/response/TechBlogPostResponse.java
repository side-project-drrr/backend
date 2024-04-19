package com.drrr.techblogpost.response;

import com.drrr.domain.category.dto.CategoryDto;
import com.drrr.domain.techblogpost.cache.payload.RedisSlicePostsContents;
import com.drrr.domain.techblogpost.dto.TechBlogPostCategoryDto;
import com.drrr.domain.techblogpost.dto.TechBlogPostDynamicDto;
import com.drrr.domain.techblogpost.dto.TechBlogPostSliceDto;
import com.drrr.domain.techblogpost.dto.TechBlogPostStaticDataDto;
import java.util.List;
import lombok.Builder;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

@Builder
public record TechBlogPostResponse(
        TechBlogPostStaticDataDto techBlogPostStaticDataDto,
        TechBlogPostDynamicDto techBlogPostDynamicDto,
        List<CategoryDto> categoryDto
) {
    public static Slice<TechBlogPostResponse> from(
            final List<TechBlogPostCategoryDto> contents,
            final boolean hasNext,
            final Pageable pageable
    ) {
        List<TechBlogPostResponse> techBlogPostResponses = contents.stream()
                .map(content -> TechBlogPostResponse.builder()
                        .techBlogPostStaticDataDto(content.techBlogPostStaticDataDto())
                        .categoryDto(content.categoryDto())
                        .techBlogPostDynamicDto(content.techBlogPostDynamicDto())
                        .build())
                .toList();
        return new SliceImpl<>(techBlogPostResponses, pageable, hasNext);
    }

    public static Slice<TechBlogPostResponse> from(final TechBlogPostSliceDto techBlogPostSliceDto) {

        return new SliceImpl<>(TechBlogPostResponse.from(techBlogPostSliceDto.contents())
                , techBlogPostSliceDto.pageable(),
                techBlogPostSliceDto.hasNext());
    }

    public static List<TechBlogPostResponse> from(final List<TechBlogPostCategoryDto> contents) {
        return contents.stream()
                .map(content -> TechBlogPostResponse.builder()
                        .techBlogPostStaticDataDto(content.techBlogPostStaticDataDto())
                        .categoryDto(content.categoryDto())
                        .techBlogPostDynamicDto(content.techBlogPostDynamicDto())
                        .build())
                .toList();
    }

    public static List<TechBlogPostResponse> fromRedis(final List<RedisSlicePostsContents> contents) {
        return contents.stream()
                .map((redisEntity) -> TechBlogPostResponse.builder()
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
