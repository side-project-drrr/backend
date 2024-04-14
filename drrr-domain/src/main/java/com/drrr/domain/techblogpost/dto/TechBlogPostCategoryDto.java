package com.drrr.domain.techblogpost.dto;

import com.drrr.core.code.techblog.TopTechBlogType;
import com.drrr.domain.category.dto.CategoryDto;
import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import lombok.Builder;

@Builder
public record TechBlogPostCategoryDto(
        TechBlogPostStaticDataDto techBlogPostStaticDataDto,
        TechBlogPostDynamicDto techBlogPostDynamicDto,
        List<CategoryDto> categoryDto
) implements Serializable {
    public static Comparator<TechBlogPostCategoryDto> orderByTopBlogTypeCondition(final TopTechBlogType type) {
        if (type.equals(TopTechBlogType.VIEWS)) {
            //List<TechBlogPostCategoryDto>에서 ViewCount로 정렬하는 Comparator를 구현
            return Comparator.comparingInt((TechBlogPostCategoryDto o) -> o.techBlogPostDynamicDto().viewCount())
                    .reversed();
        }
        return Comparator.comparingInt((TechBlogPostCategoryDto o) -> o.techBlogPostDynamicDto().likeCount())
                .reversed();
    }
}
