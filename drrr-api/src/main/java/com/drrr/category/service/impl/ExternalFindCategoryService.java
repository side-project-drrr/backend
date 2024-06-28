package com.drrr.category.service.impl;

import com.drrr.category.request.CategoryIndexSliceRequest;
import com.drrr.category.request.CategoryRangeRequest;
import com.drrr.core.category.constant.CategoryTypeConstants;
import com.drrr.domain.category.dto.CategoryDto;
import com.drrr.domain.category.dto.CategoryRangeDto;
import com.drrr.domain.category.repository.CategoryRepository;
import com.drrr.domain.category.service.CategoryService;
import com.drrr.web.page.request.CategoryIndexPageableRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
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

    public Slice<CategoryDto> execute(final CategoryIndexPageableRequest pageRequest) {
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

    public Slice<CategoryDto> execute(final CategoryIndexSliceRequest request,
                                      final CategoryIndexPageableRequest pageableRequest) {

        //기타 카테고리
        if (request.type().equals(CategoryTypeConstants.ETC)) {
            return categoryRepository.findEtcCategoriesPage(pageableRequest.fromPageRequest());
        }

        //한글 or 영어 카테고리
        return categoryRepository.findCategoryByNameLike(request.type()
                , request.index()
                , pageableRequest.fromPageRequest());
    }
}
