package com.drrr.category.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record CategorySearchWordRequest(
        @NotNull
        String keyword
) {
}
