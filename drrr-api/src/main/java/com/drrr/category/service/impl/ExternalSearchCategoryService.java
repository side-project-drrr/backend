package com.drrr.category.service.impl;

import com.drrr.category.request.CategorySearchWordRequest;
import com.drrr.domain.category.dto.CategoryDto;
import com.drrr.domain.category.repository.CategoryRepository;
import com.drrr.web.page.request.PageableRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExternalSearchCategoryService {
    private final CategoryRepository categoryRepository;

    public Slice<CategoryDto> execute(final CategorySearchWordRequest request, final PageableRequest pageableRequest) {
        return categoryRepository.searchCategoriesByKeyWord(request.keyword(), pageableRequest.fromPageRequest());
    }
}
