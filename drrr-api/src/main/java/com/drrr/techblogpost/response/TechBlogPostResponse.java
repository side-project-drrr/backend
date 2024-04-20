package com.drrr.techblogpost.response;

import com.drrr.core.code.techblog.TechBlogCode;
import com.drrr.domain.category.dto.CategoryDto;
import com.drrr.domain.techblogpost.cache.payload.RedisSlicePostsContents;
import com.drrr.domain.techblogpost.dto.TechBlogPostCategoryDto;
import com.drrr.domain.techblogpost.dto.TechBlogPostSliceDto;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

@Builder
public record TechBlogPostResponse(
        Long id,
        String title,
        String summary,
        TechBlogCode techBlogCode,
        String thumbnailUrl,
        LocalDate writtenAt,
        String url,
        int viewCount,
        int likeCount,
        List<CategoryDto> categoryDto
) {
    public static Slice<TechBlogPostResponse> from(
            final List<TechBlogPostCategoryDto> contents,
            final boolean hasNext,
            final Pageable pageable
    ) {
        List<TechBlogPostResponse> techBlogPostResponses = createTechBlogPostResponse(contents);
        return new SliceImpl<>(techBlogPostResponses, pageable, hasNext);
    }

    public static Slice<TechBlogPostResponse> from(final TechBlogPostSliceDto techBlogPostSliceDto) {

        return new SliceImpl<>(TechBlogPostResponse.from(techBlogPostSliceDto.contents())
                , techBlogPostSliceDto.pageable(),
                techBlogPostSliceDto.hasNext());
    }

    public static List<TechBlogPostResponse> from(final List<TechBlogPostCategoryDto> contents) {
        return createTechBlogPostResponse(contents);
    }

    public static List<TechBlogPostResponse> fromRedis(final List<RedisSlicePostsContents> contents) {
        return contents.stream()
                .map((redisEntity) -> TechBlogPostResponse.builder()
                        .id(redisEntity.redisTechBlogPostStaticData().id())
                        .title(redisEntity.redisTechBlogPostStaticData().title())
                        .summary(redisEntity.redisTechBlogPostStaticData().summary())
                        .techBlogCode(redisEntity.redisTechBlogPostStaticData().techBlogCode())
                        .thumbnailUrl(redisEntity.redisTechBlogPostStaticData().thumbnailUrl())
                        .writtenAt(redisEntity.redisTechBlogPostStaticData().writtenAt())
                        .url(redisEntity.redisTechBlogPostStaticData().url())
                        .viewCount(redisEntity.redisTechBlogPostDynamicData().getViewCount())
                        .likeCount(redisEntity.redisTechBlogPostDynamicData().getLikeCount())
                        .categoryDto(redisEntity.redisCategories().stream()
                                .map(redisCategory -> CategoryDto.builder()
                                        .id(redisCategory.id())
                                        .name(redisCategory.name())
                                        .build())
                                .toList()).build()).toList();
    }

    private static List<TechBlogPostResponse> createTechBlogPostResponse(final List<TechBlogPostCategoryDto> contents) {
        return contents.stream()
                .map(content -> TechBlogPostResponse.builder()
                        .id(content.techBlogPostStaticDataDto().id())
                        .title(content.techBlogPostStaticDataDto().title())
                        .summary(content.techBlogPostStaticDataDto().summary())
                        .techBlogCode(content.techBlogPostStaticDataDto().techBlogCode())
                        .thumbnailUrl(content.techBlogPostStaticDataDto().thumbnailUrl())
                        .writtenAt(content.techBlogPostStaticDataDto().writtenAt())
                        .url(content.techBlogPostStaticDataDto().url())
                        .viewCount(content.techBlogPostDynamicDto().viewCount())
                        .likeCount(content.techBlogPostDynamicDto().likeCount())
                        .categoryDto(content.categoryDto())
                        .build())
                .toList();
    }


}
