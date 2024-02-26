package com.drrr.category.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record CategorySliceRequest(
        @NotNull
        int page,
        @NotNull
        int size
) {
}
