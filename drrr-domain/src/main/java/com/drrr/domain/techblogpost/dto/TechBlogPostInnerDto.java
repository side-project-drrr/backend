package com.drrr.domain.techblogpost.dto;

import com.drrr.core.code.techblog.TechBlogCode;
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
}
