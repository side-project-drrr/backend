package com.drrr.domain.techblogpost.dto;

import java.util.List;
import lombok.Builder;

@Builder
public record TechBlogPostContentDto(
        TechBlogPostStaticDataDto techBlogPostStaticDataDto,
        TechBlogPostDynamicDto techBlogPostDynamicDto
) {

    public static List<TechBlogPostContentDto> from(final List<TechBlogPostBasicInfo> postEntities) {
        return postEntities.stream()
                .map(dto -> {
                    TechBlogPostStaticDataDto postStaticData = TechBlogPostStaticDataDto.builder()
                            .id(dto.id())
                            .title(dto.title())
                            .summary(dto.summary())
                            .techBlogCode(dto.techBlogCode())
                            .thumbnailUrl(dto.thumbnailUrl())
                            .writtenAt(dto.writtenAt())
                            .url(dto.url())
                            .build();
                    TechBlogPostDynamicDto postDynamic = TechBlogPostDynamicDto.builder()
                            .likeCount(dto.postLike())
                            .viewCount(dto.viewCount())
                            .build();
                    return TechBlogPostContentDto.builder()
                            .techBlogPostDynamicDto(postDynamic)
                            .techBlogPostStaticDataDto(postStaticData)
                            .build();
                })
                .toList();
    }
}
