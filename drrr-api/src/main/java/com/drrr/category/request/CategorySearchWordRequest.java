package com.drrr.category.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record CategorySearchWordRequest(
        @NotNull
        int page,
        @NotNull
        int size,
        @NotNull
        String keyword
) {
}
