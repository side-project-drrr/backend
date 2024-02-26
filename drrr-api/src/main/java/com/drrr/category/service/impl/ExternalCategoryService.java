package com.drrr.category.service.impl;

import com.drrr.domain.category.dto.CategoryDto;
import com.drrr.domain.category.entity.Category;
import com.drrr.domain.category.entity.RedisCategory;
import com.drrr.domain.category.repository.CategoryRepository;
import com.drrr.domain.category.service.CategoryService;
import com.drrr.domain.category.service.RedisCategoryService;
import com.drrr.domain.jpa.entity.BaseEntity;
import com.drrr.web.page.request.PageableRequest;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExternalCategoryService {
    private final CategoryService categoryService;
    private final RedisCategoryService redisCategoryService;
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
        final List<Category> redisCategories = redisCategoryService.findByIds(categoryIds);

        if (redisCategories.size() == categoryIds.size()) {
            return categoryService.findSelectedCategories(categoryIds);
        }

        return registerNewCategoryToCache(categoryIds, redisCategories);
    }

    private List<CategoryDto> registerNewCategoryToCache(List<Long> categoryIds, List<Category> redisCategories) {
        //redis에 없는 경우
        final Set<Long> requestIds = new HashSet<>(categoryIds);
        redisCategories.stream().map(BaseEntity::getId)
                .forEach(requestIds::remove);

        final List<Long> ids = requestIds.stream().toList();

        final List<CategoryDto> selectedCategories = categoryService.findSelectedCategories(ids);

        final List<RedisCategory> redisCategoryList = selectedCategories.stream()
                .filter(redisCategory -> requestIds.contains(redisCategory.id()))
                .map(filteredCategory -> RedisCategory.builder()
                        .id(filteredCategory.id())
                        .name(filteredCategory.name())
                        .build())
                .toList();

        redisCategoryService.saveCategories(redisCategoryList);

        //redis에 기존에 있었던 카테고리와 없는 카테고리의 정보를 합쳐서 반환
        return Stream.concat(
                        redisCategories.stream()
                                .map(redisCategory -> CategoryDto.builder()
                                        .id(redisCategory.getId())
                                        .name(redisCategory.getName())
                                        .build()),
                        selectedCategories.stream())
                .collect(Collectors.toList());
    }
}
