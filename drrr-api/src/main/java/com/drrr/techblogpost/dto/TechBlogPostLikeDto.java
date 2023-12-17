package com.drrr.techblogpost.dto;

import lombok.Builder;

@Builder
public record TechBlogPostLikeDto(
        Long memberId,
        Long postId
) {
}
