package com.drrr.recommand.dto;

import com.drrr.core.code.techblog.TechBlogCode;
import com.drrr.domain.techblogpost.entity.TechBlogPost;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record TechBlogPostDto(
        Long id,
        LocalDate createdDate,
        String thumbnailUrl,
        String title,
        String summary,
        String urlSuffix,
        String url,
        TechBlogCode techBlogCode,
        int viewCount
) {
    public static TechBlogPostDto from(TechBlogPost post) {
        return TechBlogPostDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .createdDate(post.getWrittenAt())
                .url(post.getUrl())
                .urlSuffix(post.getUrlSuffix())
                .summary(post.getSummary())
                .techBlogCode(post.getTechBlogCode())
                .thumbnailUrl(post.getThumbnailUrl())
                .viewCount(post.getViewCount())
                .build();
    }
}
