package com.drrr.domain.techblogpost.dto;

import lombok.Builder;

@Builder
public record TechBlogPostDynamicDto(
        int viewCount,
        int likeCount
) {
}
