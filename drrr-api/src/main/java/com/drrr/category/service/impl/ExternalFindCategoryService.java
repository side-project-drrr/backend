package com.drrr.category.service.impl;

import com.drrr.domain.category.dto.CategoryDto;
import com.drrr.domain.category.service.CategoryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExternalFindCategoryService {
    private final CategoryService categoryService;

    public List<CategoryDto> execute(final Long postId) {
        return categoryService.findCategoriesByPostId(postId);
    }
}
