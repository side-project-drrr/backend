package com.drrr.category.request;

import com.drrr.core.category.constant.IndexConstants;
import com.drrr.core.category.constant.LanguageConstants;
import lombok.Builder;

@Builder
public record CategoryIndexSliceRequest(
        int page,
        int size,
        String sort,
        String direction,
        LanguageConstants language,
        IndexConstants index
) {

}
