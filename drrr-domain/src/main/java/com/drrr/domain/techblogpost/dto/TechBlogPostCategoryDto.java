package com.drrr.domain.techblogpost.dto;

import com.drrr.domain.category.dto.CategoryDto;
import java.io.Serializable;
import java.util.List;
import lombok.Builder;

@Builder
public record TechBlogPostCategoryDto(
        TechBlogPostBasicInfoDto techBlogPostBasicInfoDto,
        List<CategoryDto> categoryDto
) implements Serializable {
}
