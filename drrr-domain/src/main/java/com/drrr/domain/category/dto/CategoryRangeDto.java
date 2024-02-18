package com.drrr.domain.category.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.Builder;

@Builder
public record CategoryRangeDto(
        List<Content> content
) {
    @Builder
    public record Content(
            List<CategoryDto> category,
            String keyIndex
    ) {
    }

    public static CategoryRangeDto from(Map<String, List<CategoryDto>> postCategories) {
        List<CategoryRangeDto.Content> categoryRangeResponses = new ArrayList<>();

        postCategories.forEach((key, value) -> {
            CategoryRangeDto.Content content = CategoryRangeDto.Content.builder()
                    .category(postCategories.get(key))
                    .keyIndex(key)
                    .build();
            categoryRangeResponses.add(content);
        });

        return CategoryRangeDto.builder()
                .content(categoryRangeResponses)
                .build();
    }
}
