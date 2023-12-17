package com.drrr.domain.category.service;

import com.drrr.core.exception.category.CategoryExceptionCode;
import com.drrr.domain.category.entity.Category;
import com.drrr.domain.category.repository.CategoryRepository;
import java.util.Comparator;
import java.util.List;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public List<CategoryDto> findAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        if (categories.isEmpty()) {
            log.error("카테고리가 존재하지 않습니다.");
            throw CategoryExceptionCode.CATEGORY_NOT_FOUND.newInstance();
        }
        return categories.stream()
                .map(category -> CategoryDto.builder()
                        .id(category.getId())
                        .categoryName(category.getName())
                        .build())
                .sorted(Comparator.comparing(o -> o.categoryName))
                .toList();
    }

    public List<CategoryDto> findSelectedCategories(List<Long> ids) {
        List<Category> categories = categoryRepository.findByIdIn(ids);

        if (categories.isEmpty()) {
            log.error("카테고리가 존재하지 않습니다.");
            throw CategoryExceptionCode.CATEGORY_NOT_FOUND.newInstance();
        }

        return categories.stream()
                .map(category -> CategoryDto.builder()
                        .id(category.getId())
                        .categoryName(category.getName())
                        .build())
                .sorted(Comparator.comparing(o -> o.categoryName))
                .toList();
    }

    @Builder
    public record CategoryDto(
            Long id,
            String categoryName
    ) {

    }
}
