package com.drrr.domain.techblogpost.dto;

import com.drrr.core.code.techblog.TopTechBlogType;
import com.drrr.domain.category.dto.CategoryDto;
import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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

    public static List<TechBlogPostCategoryDto> inOrderFrom(
            final List<TechBlogPostContentDto> contents,
            final TopTechBlogType type,
            final Map<Long, List<CategoryDto>> postIdsCategories
    ) {
        return contents.stream()
                .map(content -> TechBlogPostCategoryDto.builder()
                        .techBlogPostStaticDataDto(content.techBlogPostStaticDataDto())
                        .categoryDto(postIdsCategories.get(content.techBlogPostStaticDataDto().id()))
                        .techBlogPostDynamicDto(content.techBlogPostDynamicDto())
                        .build())
                .sorted(TechBlogPostCategoryDto.orderByTopBlogTypeCondition(type))
                .toList();
    }

    public static List<TechBlogPostCategoryDto> from(
            final List<TechBlogPostContentDto> contents,
            final Map<Long, List<CategoryDto>> postIdsCategories
    ) {
        return contents.stream()
                .map(content -> TechBlogPostCategoryDto.builder()
                        .techBlogPostStaticDataDto(content.techBlogPostStaticDataDto())
                        .categoryDto(postIdsCategories.get(content.techBlogPostStaticDataDto().id()))
                        .techBlogPostDynamicDto(content.techBlogPostDynamicDto())
                        .build())
                .collect(Collectors.toList());
    }
}
