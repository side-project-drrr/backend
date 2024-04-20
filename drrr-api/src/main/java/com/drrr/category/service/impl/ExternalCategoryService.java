package com.drrr.category.service.impl;

import com.drrr.domain.category.dto.CategoryDto;
import com.drrr.domain.category.repository.CategoryRepository;
import com.drrr.domain.category.service.CategoryService;
import com.drrr.web.page.request.PageableRequest;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExternalCategoryService {
    private final CategoryService categoryService;
    private final CategoryRepository categoryRepository;

    /**
     * 모든 카테고리 정보를 가져옴, redis를 사용하지 않음 파라미터 없이 redis에서 findAll할 경우 문제가 발생할 수 있음 기존에 20개중 3개가 들어갔다고 하면 3개만 끌고 오는 문제 발생
     */
    public Slice<CategoryDto> execute(final PageableRequest request) {
        final PageRequest pageRequest = request.fromPageRequest();
        return categoryRepository.findAll(pageRequest).map(CategoryDto::from);
    }

    public List<CategoryDto> execute(final Long count) {
        return categoryService.findTopCategories(count);
    }

    /**
     * 지정된 카테고리 정보를 가져옴
     */
    public List<CategoryDto> execute(final List<Long> categoryIds) {
        return categoryService.findSelectedCategories(categoryIds);
    }
}
