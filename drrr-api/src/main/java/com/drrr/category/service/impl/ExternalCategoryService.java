package com.drrr.category.service.impl;

import com.drrr.category.dto.CategoryRequest;
import com.drrr.domain.category.entity.Category;
import com.drrr.domain.category.entity.RedisCategory;
import com.drrr.domain.category.service.CategoryService;
import com.drrr.domain.category.service.CategoryService.CategoryDto;
import com.drrr.domain.category.service.RedisCategoryService;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExternalCategoryService {
    private final CategoryService categoryService;
    private final RedisCategoryService redisCategoryService;

    /**
     * 모든 카테고리 정보를 가져옴
     */
    public List<CategoryDto> execute() {
        final List<Category> redisCategories = redisCategoryService.findAll();
        //최신 카테고리가 업데이트되는 순간 모든 캐시를 날림
        if (redisCategories.isEmpty()) {
            final List<CategoryDto> categories = categoryService.findAllCategories();

            final List<RedisCategory> categoryList = categories.stream()
                    .map(categoryDto -> RedisCategory.builder()
                            .id(categoryDto.id())
                            .name(categoryDto.categoryName())
                            .build()).toList();
            redisCategoryService.saveCategories(categoryList);
            return categories;
        }

        return redisCategories.stream().map(redisCategory -> CategoryDto.builder()
                        .id(redisCategory.getId())
                        .categoryName(redisCategory.getName())
                        .build())
                .toList();
    }

    /**
     * 지정된 카테고리 정보를 가져옴
     */
    public List<CategoryDto> execute(final CategoryRequest request) {
        final List<Category> redisCategories = redisCategoryService.findByIds(request.categoryIds());
        if (redisCategories.size() != request.categoryIds().size()) {
            final Set<Long> requestIds = request.categoryIds().stream().collect(Collectors.toSet());
            final Set<Long> redisCategoryIds = redisCategories.stream().map(redisCategory -> redisCategory.getId())
                    .collect(Collectors.toSet());
            //redis에 없는 카테고리를 뽑아냄
            requestIds.removeAll(redisCategoryIds);

            final List<Long> ids = requestIds.stream().toList();

            final List<CategoryDto> selectedCategories = categoryService.findSelectedCategories(ids);

            final List<RedisCategory> redisCategoryList = selectedCategories.stream()
                    .filter(redisCategory -> requestIds.contains(redisCategory.id()))
                    .map(filteredCategory -> RedisCategory.builder()
                            .id(filteredCategory.id())
                            .name(filteredCategory.categoryName())
                            .build())
                    .toList();

            redisCategoryService.saveCategories(redisCategoryList);
            //redis에 기존에 있었던 카테고리 dto로 변환
            final List<CategoryDto> existingCategories = redisCategories.stream()
                    .map(redisCategory -> CategoryDto.builder()
                            .id(redisCategory.getId())
                            .categoryName(redisCategory.getName())
                            .build())
                    .toList();

            if (!existingCategories.isEmpty()) {
                selectedCategories.addAll(existingCategories);
            }

            return selectedCategories;
        }
        return categoryService.findSelectedCategories(request.categoryIds());
    }
}
