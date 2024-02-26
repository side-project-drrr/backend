package com.drrr.category.request;

import com.drrr.core.category.constant.IndexConstants;
import com.drrr.core.category.constant.LanguageConstants;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record CategoryIndexSliceRequest(
        @NotNull
        int page,
        @NotNull

        int size,
        @NotNull
        LanguageConstants language,
        @NotNull
        IndexConstants index
) {

}
