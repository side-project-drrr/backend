package com.drrr.techblogpost.request;

import lombok.Builder;

@Builder
public record TechBlogPostSliceRequest(
        int page,
        int size,
        String sort,
        String direction
) {

}
