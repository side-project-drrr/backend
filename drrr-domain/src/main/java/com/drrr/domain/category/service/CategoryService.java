package com.drrr.domain.category.service;

import static com.drrr.domain.category.entity.QCategory.category;
import static com.drrr.domain.category.entity.QCategoryWeight.categoryWeight;

import com.drrr.core.exception.category.CategoryExceptionCode;
import com.drrr.domain.category.entity.Category;
import com.drrr.domain.category.repository.CategoryRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Comparator;
import java.util.List;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public List<CategoryDto> findTopCategories(final Long count) {
        //사용자들의 총 선호 카테고리를 group by 해서 선호도가 가장 많은 카테고리 순으로 반환
        List<Category> categories = categoryRepository.findTopCategories(count);

        if (categories.isEmpty()) {
            log.error("카테고리가 존재하지 않습니다 -> {}", categories);
            throw CategoryExceptionCode.CATEGORY_NOT_FOUND.newInstance();
        }

        return categories.stream()
                .map(category -> CategoryDto.builder()
                        .id(category.getId())
                        .categoryName(category.getName())
                        .build())
                .toList();
    }

    public List<CategoryDto> findAllCategories() {
        final List<Category> categories = categoryRepository.findAllOrderByNames();
        return getCategoryDtos(categories);
    }

    @NotNull
    private List<CategoryDto> getCategoryDtos(List<Category> categories) {
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

    public List<CategoryDto> findSelectedCategories(final List<Long> ids) {
        final List<Category> categories = categoryRepository.findByIdIn(ids);

        return getCategoryDtos(categories);
    }

    @Builder
    public record CategoryDto(
            Long id,
            String categoryName
    ) {

    }
}
