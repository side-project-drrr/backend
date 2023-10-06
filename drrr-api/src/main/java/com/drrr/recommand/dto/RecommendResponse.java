package com.drrr.recommand.dto;


import java.util.List;
import lombok.Builder;

@Builder
public record RecommendResponse(
        List<TechBlogPostDto> posts
) {
}
