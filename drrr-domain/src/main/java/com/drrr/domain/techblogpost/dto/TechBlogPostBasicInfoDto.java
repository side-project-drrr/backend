package com.drrr.domain.techblogpost.dto;

import com.drrr.core.code.techblog.TechBlogCode;
import com.drrr.domain.techblogpost.entity.TechBlogPost;
import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;

@Builder
public record TechBlogPostBasicInfoDto(
        Long id,
        String title,
        String summary,
        TechBlogCode techBlogCode,
        String thumbnailUrl,
        int viewCount,
        int postLike,
        LocalDate writtenAt

) {
    @QueryProjection
    public TechBlogPostBasicInfoDto(Long id, String title, String summary, TechBlogCode techBlogCode,
                                    String thumbnailUrl,
                                    int viewCount, int postLike, LocalDate writtenAt) {
        this.id = id;
        this.title = title;
        this.summary = summary;
        this.techBlogCode = techBlogCode;
        this.thumbnailUrl = thumbnailUrl;
        this.viewCount = viewCount;
        this.postLike = postLike;
        this.writtenAt = writtenAt;
    }

    static public List<TechBlogPostBasicInfoDto> from(final List<TechBlogPost> posts) {
        return posts.stream()
                .map(post -> TechBlogPostBasicInfoDto.builder()
                        .title(post.getTitle())
                        .techBlogCode(post.getTechBlogCode())
                        .summary(post.getSummary())
                        .postLike(post.getPostLike())
                        .thumbnailUrl(post.getThumbnailUrl())
                        .viewCount(post.getViewCount())
                        .id(post.getId())
                        .writtenAt(post.getWrittenAt())
                        .build())
                .toList();
    }

    static public TechBlogPostBasicInfoDto from(final TechBlogPost post) {
        return TechBlogPostBasicInfoDto.builder()
                .title(post.getTitle())
                .techBlogCode(post.getTechBlogCode())
                .summary(post.getSummary())
                .postLike(post.getPostLike())
                .thumbnailUrl(post.getThumbnailUrl())
                .viewCount(post.getViewCount())
                .id(post.getId())
                .writtenAt(post.getWrittenAt())
                .build();
    }

}
