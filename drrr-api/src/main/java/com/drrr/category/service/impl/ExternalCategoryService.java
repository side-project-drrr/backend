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

    public List<CategoryDto> execute() {
        List<Category> redisCategories = redisCategoryService.findAll();
        //최신 카테고리가 업데이트되는 순간 모든 캐시를 날림
        if (redisCategories.isEmpty()) {
            List<CategoryDto> categories = categoryService.findAllCategories();

            List<RedisCategory> categoryList = categories.stream()
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

    public List<CategoryDto> execute(CategoryRequest request) {
        List<Category> redisCategories = redisCategoryService.findByIds(request.categoryIds());
        if (redisCategories.size() != request.categoryIds().size()) {
            Set<Long> requestIds = request.categoryIds().stream().collect(Collectors.toSet());
            Set<Long> redisCategoryIds = redisCategories.stream().map(redisCategory -> redisCategory.getId())
                    .collect(Collectors.toSet());
            requestIds.removeAll(redisCategoryIds);

            List<Long> ids = requestIds.stream().toList();

            List<CategoryDto> selectedCategories = categoryService.findSelectedCategories(ids);

            List<RedisCategory> redisCategoryList = selectedCategories.stream()
                    .filter(redisCategory -> requestIds.contains(redisCategory.id()))
                    .map(filteredCategory -> RedisCategory.builder()
                            .id(filteredCategory.id())
                            .name(filteredCategory.categoryName())
                            .build())
                    .toList();
            redisCategoryService.saveCategories(redisCategoryList);
        }
        return categoryService.findSelectedCategories(request.categoryIds());
    }
}
