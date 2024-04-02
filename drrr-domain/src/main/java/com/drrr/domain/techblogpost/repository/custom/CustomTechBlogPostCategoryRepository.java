package com.drrr.domain.techblogpost.repository.custom;

import com.drrr.domain.category.dto.ExtractedPostCategoryDto;
import java.util.List;


public interface CustomTechBlogPostCategoryRepository {
    List<ExtractedPostCategoryDto> findPostsByCategoryIdsNotInLog(final List<Long> categoryId,
                                                                  final Long memberId);

}
