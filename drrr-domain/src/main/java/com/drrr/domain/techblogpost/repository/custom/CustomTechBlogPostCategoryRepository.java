package com.drrr.domain.techblogpost.repository.custom;

import com.drrr.domain.category.dto.CategoryWeightDto;
import com.drrr.domain.category.service.RecommendPostService.ExtractedPostCategoryDto;
import java.util.List;


public interface CustomTechBlogPostCategoryRepository {
    List<ExtractedPostCategoryDto> getFilteredPost(List<CategoryWeightDto> categoryWeightDtos, Long memberId);

}
