package com.drrr.domain.techblogpost.dto;

import com.drrr.core.code.techblog.TechBlogCode;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record TechBlogPostOuterDto(
        Long id,
        String title,
        String summary,
        TechBlogCode techBlogCode,
        String thumbnailUrl,
        int viewCount,
        int postLike,
        LocalDate writtenAt

) {
}
