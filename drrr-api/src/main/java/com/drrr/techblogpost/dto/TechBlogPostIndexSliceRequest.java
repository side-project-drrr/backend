package com.drrr.techblogpost.dto;

import lombok.Builder;

@Builder
public record TechBlogPostIndexSliceRequest(
        int page,
        int size,
        String sort,
        String direction,
        String keyword
) {

}
