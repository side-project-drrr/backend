package com.drrr.domain.category.dto;

import com.querydsl.core.annotations.QueryProjection;

public record CategoryPostDto(
        Long categoryId,
        Long postId,
        String name
) {
    @QueryProjection
    public CategoryPostDto(Long categoryId, Long postId, String name) {
        this.categoryId = categoryId;
        this.postId = postId;
        this.name = name;
    }
}
