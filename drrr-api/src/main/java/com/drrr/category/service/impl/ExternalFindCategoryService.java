package com.drrr.category.service.impl;

import com.drrr.category.request.CategoryIndexSliceRequest;
import com.drrr.domain.category.dto.CategoryDto;
import com.drrr.domain.category.service.CategoryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExternalFindCategoryService {
    private final CategoryService categoryService;

    public List<CategoryDto> execute(final Long postId) {
        return categoryService.findCategoriesByPostId(postId);
    }

    public Slice<CategoryDto> execute(final CategoryIndexSliceRequest request) {
        final Sort sort = Sort.by(Sort.Direction.fromString(request.direction()), request.sort());
        final PageRequest pageRequest = PageRequest.of(request.page(), request.size(), sort);

        return categoryService.findIndexCategory(pageRequest, request.language(), request.index());
    }
}
