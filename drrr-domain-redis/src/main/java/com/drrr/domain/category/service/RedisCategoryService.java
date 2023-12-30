package com.drrr.domain.category.service;

import com.drrr.domain.category.entity.Category;
import com.drrr.domain.category.entity.RedisCategory;
import com.drrr.domain.category.repository.RedisCategoryRepository;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
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

    public List<Category> findByIds(List<Long> categoryIds) {
        //Long를 String으로 변경
        final List<String> keys = categoryIds.stream()
                .map(Object::toString)
                .toList();

        //key 해당하는 value가 없으면 filtering
        return redisCategoryTemplate.opsForValue().multiGet(keys).stream()
                .filter(Objects::nonNull).map(category -> Category.builder()
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
