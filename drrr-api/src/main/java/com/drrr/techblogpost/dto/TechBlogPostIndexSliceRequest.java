package com.drrr.techblogpost.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record TechBlogPostIndexSliceRequest(
        @NotNull
        int page,
        @NotNull
        int size,
        @NotNull
        String keyword
) {

}
