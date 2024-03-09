package com.drrr.domain.category.service;

import com.drrr.domain.category.entity.Category;
import com.drrr.domain.category.entity.RedisCategory;
import com.drrr.domain.category.repository.RedisCategoryRepository;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class RedisCategoryService {
    private final RedisCategoryRepository redisCategoryRepository;
    private final RedisTemplate<String, RedisCategory> redisCategoryTemplate;

    public List<Category> findByIds(final List<Long> categoryIds) {
        final List<String> keys = categoryIds.stream()
                .map(Object::toString)
                .toList();

        return Optional.ofNullable(redisCategoryTemplate.opsForValue().multiGet(keys))
                .map(categories -> categories.stream()
                        .filter(Objects::nonNull)
                        .map(RedisCategory::name)
                        .map(Category::new)
                        .sorted(Comparator.comparing(Category::getName)).toList())
                .orElse(List.of());
    }


    public void saveCategories(final List<RedisCategory> categories) {
        List<RedisCategory> redisCategories = categories.stream()
                .map(category -> RedisCategory.builder()
                        .id(category.id())
                        .name(category.name())
                        .build())
                .toList();

        redisCategoryRepository.saveAll(redisCategories);
    }
}
