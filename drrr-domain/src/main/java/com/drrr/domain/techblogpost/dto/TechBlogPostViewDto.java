package com.drrr.domain.techblogpost.dto;

import com.drrr.core.code.techblog.TechBlogCode;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record TechBlogPostViewDto(
        Long id,
        String title,
        String summary,
        TechBlogCode techBlogCode,
        String thumbnailUrl,
        LocalDate writtenAt,
        String url,
        int viewCount,
        int likeCount
) {
}
