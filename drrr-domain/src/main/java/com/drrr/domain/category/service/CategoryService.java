package com.drrr.domain.category.service;

import com.drrr.core.category.constant.CategoryTypeConstants;
import com.drrr.core.category.constant.IndexConstants;
import com.drrr.domain.category.dto.CategoryDto;
import com.drrr.domain.category.dto.CategoryRangeDto;
import com.drrr.domain.category.entity.Category;
import com.drrr.domain.category.repository.CategoryRepository;
import com.drrr.domain.category.repository.impl.CustomCategoryRepositoryImpl.CategoriesKeyDto;
import com.drrr.domain.exception.DomainExceptionCode;
import jakarta.validation.constraints.NotNull;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public List<CategoryDto> findCategoriesByPostId(final Long postId) {
        List<CategoryDto> categories = categoryRepository.findCategoriesByPostId(postId);

        if (categories.isEmpty()) {
            log.error("카테고리가 존재하지 않습니다 -> {}", categories);
            throw DomainExceptionCode.CATEGORY_NOT_FOUND.newInstance();
        }

        return categories;
    }


    public CategoryRangeDto findEtcCategoriesByRange(final int size) {
        final List<CategoriesKeyDto> rangedEtcCategories = categoryRepository.findRangedEtcCategories(size);

        return CategoryRangeDto.from(rangedEtcCategories.stream()
                .collect(Collectors.groupingBy(
                        CategoriesKeyDto::keyIndex,
                        LinkedHashMap::new,
                        Collectors.mapping(categoryDto -> CategoryDto.builder()
                                        .id(categoryDto.id())
                                        .name(categoryDto.name())
                                        .build(),
                                Collectors.toList())
                )));
    }

    public CategoryRangeDto findCategoriesByRange(final IndexConstants startIdx, final IndexConstants endIdx,
                                                  final CategoryTypeConstants language, final int size) {
        final List<CategoriesKeyDto> rangedEtcCategories = categoryRepository.findRangedCategories(startIdx, endIdx,
                language, size);

        return CategoryRangeDto.from(rangedEtcCategories.stream()
                .collect(Collectors.groupingBy(
                        CategoriesKeyDto::keyIndex,
                        LinkedHashMap::new,
                        Collectors.mapping(categoryDto -> CategoryDto.builder()
                                        .id(categoryDto.id())
                                        .name(categoryDto.name())
                                        .build(),
                                Collectors.toList())
                )));
    }

    public List<CategoryDto> findTopCategories(final Long count) {
        //사용자들의 총 선호 카테고리를 group by 해서 선호도가 가장 많은 카테고리 순으로 반환
        List<Category> categories = categoryRepository.findTopCategories(count);

        if (categories.isEmpty()) {
            log.error("카테고리가 존재하지 않습니다 -> {}", categories);
            throw DomainExceptionCode.CATEGORY_NOT_FOUND.newInstance();
        }

        return categories.stream()
                .map(category -> CategoryDto.builder()
                        .id(category.getId())
                        .name(category.getName())
                        .build())
                .toList();
    }

    @NotNull
    private List<CategoryDto> getCategoryDtos(final List<Category> categories) {
        if (categories.isEmpty()) {
            log.error("카테고리가 존재하지 않습니다.");
            throw DomainExceptionCode.CATEGORY_NOT_FOUND.newInstance();
        }
        return CategoryDto.from(categories);
    }

    public List<CategoryDto> findSelectedCategories(final List<Long> ids) {
        final List<Category> categories = categoryRepository.findByIdInOrderByName(ids);

        return getCategoryDtos(categories);
    }

}
