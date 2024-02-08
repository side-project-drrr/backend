package com.drrr.techblogpost.dto;

import com.drrr.core.category.constant.LanguageConstants;
import lombok.Builder;

@Builder
public record TechBlogPostIndexSliceRequest(
        int page,
        int size,
        String sort,
        String direction,
        String index
) {

}
