package com.drrr.domain.category.dto;

import com.drrr.domain.category.entity.Category;
import com.drrr.domain.member.entity.Member;
import java.time.LocalDateTime;
import lombok.Builder;


@Builder
public record CategoryWeightDto(
        Long id,
        Category category,
        Member member,
        double value,
        boolean preferred,
        LocalDateTime updatedAt
) {
    public CategoryWeightDto updateValue(double calculatedValue) {
        return new CategoryWeightDto(id, category, member, calculatedValue, preferred, updatedAt);
    }
}
