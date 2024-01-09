package com.drrr.domain.techblogpost.dto;

import com.drrr.core.code.techblog.TechBlogCode;
import com.drrr.domain.techblogpost.entity.TechBlogPost;
import lombok.Builder;

@Builder
public record TechBlogPostInnerDto(
        Long id,
        String title,
        TechBlogCode techBlogCode,
        String thumbnailUrl,
        String aiSummary,
        String url
) {
    public static TechBlogPostInnerDto from(final TechBlogPost post){
        return TechBlogPostInnerDto.builder()
                .aiSummary(post.getAiSummary())
                .techBlogCode(post.getTechBlogCode())
                .id(post.getId())
                .thumbnailUrl(post.getThumbnailUrl())
                .title(post.getTitle())
                .url(post.getUrl())
                .build();
    }
}
