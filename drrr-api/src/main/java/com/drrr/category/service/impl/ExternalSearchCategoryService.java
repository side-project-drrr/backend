package com.drrr.category.service.impl;

import com.drrr.category.request.CategorySearchWordRequest;
import com.drrr.domain.category.dto.CategoryDto;
import com.drrr.domain.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExternalSearchCategoryService {
    private final CategoryRepository categoryRepository;

    public Slice<CategoryDto> execute(final CategorySearchWordRequest request) {
        final PageRequest pageRequest = request.getPageable().fromPageRequest();
        return categoryRepository.searchCategoriesByKeyWord(request.getKeyword(), pageRequest);
    }
}
