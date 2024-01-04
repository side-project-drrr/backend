package com.drrr.recommand.dto;

import com.drrr.core.code.techblog.TechBlogCode;
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
}
