package com.drrr.category.request;

import com.drrr.core.category.constant.CategoryTypeConstants;
import com.drrr.core.category.constant.IndexConstants;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record CategoryIndexSliceRequest(
        @NotNull
        CategoryTypeConstants type,
        IndexConstants index
) {
}

