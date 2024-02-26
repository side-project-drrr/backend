package com.drrr.category.service.impl;

import com.drrr.category.request.CategoryIndexSliceRequest;
import com.drrr.category.request.CategoryRangeRequest;
import com.drrr.domain.category.dto.CategoryDto;
import com.drrr.domain.category.dto.CategoryRangeDto;
import com.drrr.domain.category.repository.CategoryRepository;
import com.drrr.domain.category.repository.impl.CustomCategoryRepositoryImpl.CategoriesKeyDto;
import com.drrr.domain.category.service.CategoryService;
import com.drrr.web.page.request.PageableRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExternalFindCategoryService {
    private final CategoryService categoryService;
    private final CategoryRepository categoryRepository;

    public List<CategoryDto> execute(final Long postId) {
        return categoryService.findCategoriesByPostId(postId);
    }

    public Slice<CategoriesKeyDto> execute(final PageableRequest pageRequest) {
        return categoryRepository.findEtcCategoriesPage(pageRequest.fromPageRequest());
    }

    public CategoryRangeDto execute(final int size) {
        return categoryService.findEtcCategoriesByRange(size);
    }

    public CategoryRangeDto execute(final CategoryRangeRequest request) {
        //request 검증
        CategoryRangeRequest.requestValidationCheck(request);

        return categoryService.findCategoriesByRange(request.startIdx(), request.endIdx(), request.language(),
                request.size());
    }

    public Slice<CategoryDto> execute(final CategoryIndexSliceRequest request) {
        final PageRequest pageRequest = request.pageableRequest().fromPageRequest();

        return categoryRepository.findCategoryByNameLike(request.language()
                , request.index()
                , pageRequest);
    }
}
