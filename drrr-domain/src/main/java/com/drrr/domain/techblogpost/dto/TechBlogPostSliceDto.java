package com.drrr.domain.techblogpost.dto;

import java.util.List;
import lombok.Builder;
import org.springframework.data.domain.Pageable;

@Builder
public record TechBlogPostSliceDto(
        List<TechBlogPostCategoryDto> contents,
        Pageable pageable,
        boolean hasNext
) {
    public static TechBlogPostSliceDto from(List<TechBlogPostCategoryDto> content, Pageable pageable, boolean hasNext) {
        return TechBlogPostSliceDto.builder()
                .contents(content)
                .pageable(pageable)
                .hasNext(hasNext)
                .build();
    }
}
