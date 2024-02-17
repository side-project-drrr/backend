package com.drrr.category.request;

import com.drrr.core.category.constant.IndexConstants;
import com.drrr.core.category.constant.LanguageConstants;
import lombok.Builder;

@Builder
public record CategoryRangeRequest(
        IndexConstants startIdx,
        IndexConstants endIdx,
        LanguageConstants language,
        int size
) {
}
