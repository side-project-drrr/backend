package com.drrr.domain.category.service;

import com.drrr.domain.category.entity.Category;
import com.drrr.domain.category.entity.RedisCategory;
import com.drrr.domain.category.repository.RedisCategoryRepository;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class RedisCategoryService {
    private final RedisCategoryRepository redisCategoryRepository;

    public List<Category> findByIds(List<Long> categoryIds) {
        List<RedisCategory> redisCategories = redisCategoryRepository.findByIdIn(categoryIds);
        return redisCategories.stream()
                .map(category -> Category.builder()
                        .name(category.getName())
                        .build()).sorted(Comparator.comparing(Category::getName)).toList();
    }

    public List<Category> findAll() {
        List<RedisCategory> redisCategories = (List<RedisCategory>) redisCategoryRepository.findAll();
        return redisCategories.stream()
                .map(category -> Category.builder()
                        .name(category.getName())
                        .build()).sorted(Comparator.comparing(Category::getName)).toList();
    }

    public void saveCategories(List<RedisCategory> categories) {
        List<RedisCategory> redisCategories = categories.stream()
                .map(category -> RedisCategory.builder()
                        .id(category.getId())
                        .name(category.getName())
                        .build())
                .toList();

        redisCategoryRepository.saveAll(redisCategories);
    }
}
