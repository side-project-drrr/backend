package com.drrr.domain.category.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;

@Builder
public record ExtractedPostCategoryDto(
        Long postId,
        Long categoryId
) {
    @QueryProjection
    public ExtractedPostCategoryDto(Long postId, Long categoryId) {
        this.postId = postId;
        this.categoryId = categoryId;
    }
}
