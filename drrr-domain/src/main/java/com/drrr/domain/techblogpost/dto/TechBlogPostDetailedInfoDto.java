package com.drrr.domain.techblogpost.dto;

import com.drrr.core.code.techblog.TechBlogCode;
import com.drrr.domain.techblogpost.entity.TechBlogPost;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record TechBlogPostDetailedInfoDto(
        Long id,
        String title,
        TechBlogCode techBlogCode,
        String thumbnailUrl,
        String aiSummary,
        LocalDate writtenDate,
        int viewCount,
        int postLikeCount,
        String author,
        String url
) {
    public static TechBlogPostDetailedInfoDto from(final TechBlogPost post) {
        return TechBlogPostDetailedInfoDto.builder()
                .aiSummary(post.getAiSummary())
                .techBlogCode(post.getTechBlogCode())
                .id(post.getId())
                .thumbnailUrl(post.getThumbnailUrl())
                .title(post.getTitle())
                .writtenDate(post.getWrittenAt())
                .viewCount(post.getViewCount())
                .postLikeCount(post.getPostLike())
                .author(post.getAuthor())
                .url(post.getUrl())
                .build();
    }
}
