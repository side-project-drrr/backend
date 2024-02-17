package com.drrr.domain.category.dto;

import java.util.List;
import lombok.Builder;

@Builder
public record CategoryRangeDto(
        List<Content> content
) {
    @Builder
    public record Content(
            List<CategoryDto> category,
            Character keyIndex
    ) {

    }
}
