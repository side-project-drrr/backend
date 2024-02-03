package com.drrr.domain.category.dto;

import com.drrr.domain.category.entity.Category;
import com.querydsl.core.annotations.QueryProjection;
import java.util.List;
import lombok.Builder;

@Builder
public record CategoryDto(
        Long id,
        String name
) {
    @QueryProjection
    public CategoryDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static List<CategoryDto> from(final List<Category> categories) {
        return categories.stream()
                .map(category -> CategoryDto.builder()
                        .id(category.getId())
                        .name(category.getName())
                        .build())
                .toList();
    }

    public static CategoryDto from(final Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }
}
