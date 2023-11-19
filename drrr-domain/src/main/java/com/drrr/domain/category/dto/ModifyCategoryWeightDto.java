package com.drrr.domain.category.dto;

import com.drrr.domain.category.entity.Category;
import com.drrr.domain.member.entity.Member;
import com.querydsl.core.annotations.QueryProjection;


public record ModifyCategoryWeightDto(
        Long categoryId,
        Long id,
        Category category,
        Member member,
        double value,
        boolean preferred
) {
    @QueryProjection
    public ModifyCategoryWeightDto(Long categoryId, Long id, Category category, Member member, double value,
                                   boolean preferred) {
        this.categoryId = categoryId;
        this.id = id;
        this.category = category;
        this.member = member;
        this.value = value;
        this.preferred = preferred;
    }
}
