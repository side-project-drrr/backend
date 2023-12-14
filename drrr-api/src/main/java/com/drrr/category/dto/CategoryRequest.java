package com.drrr.category.dto;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Builder;

@Builder
public record CategoryRequest(
        @NotNull List<Long> categoryIds
) {
}
