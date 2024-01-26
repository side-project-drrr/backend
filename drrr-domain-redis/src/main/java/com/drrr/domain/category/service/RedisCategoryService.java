package com.drrr.domain.category.service;

import com.drrr.domain.category.entity.Category;
import com.drrr.domain.category.entity.RedisCategory;
import com.drrr.domain.category.entity.RedisCategoryPosts;
import com.drrr.domain.category.entity.RedisCategoryPosts.CompoundCategoryId;
import com.drrr.domain.category.repository.RedisCategoryRepository;
import com.drrr.domain.techblogpost.entity.TechBlogPost;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
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
    private final RedisTemplate<String, RedisCategoryPosts> redisCategoryPostsRedisTemplate;

    public List<Category> findByIds(final List<Long> categoryIds) {
        final List<String> keys = categoryIds.stream()
                .map(Object::toString)
                .toList();

        return Optional.ofNullable(redisCategoryTemplate.opsForValue().multiGet(keys))
                .map(categories -> categories.stream()
                        .filter(Objects::nonNull)
                        .map(RedisCategory::getName)
                        .map(Category::new)
                        .sorted(Comparator.comparing(Category::getName)).toList())
                .orElse(List.of());
    }

    public Set<TechBlogPost> findPostsByCompoundIds(final List<CompoundCategoryId> categoryIds) {
        final List<String> keys = categoryIds.stream()
                .map(Object::toString)
                .toList();

        return Optional.ofNullable(redisCategoryPostsRedisTemplate.opsForValue().multiGet(keys))
                .map(posts -> posts.stream()
                        .filter(Objects::nonNull)
                        .flatMap(redisCategoryPosts -> redisCategoryPosts.getPosts().stream())
                        .collect(Collectors.toSet()))
                .orElse(Collections.emptySet());
    }

    public void saveCategories(final List<RedisCategory> categories) {
        List<RedisCategory> redisCategories = categories.stream()
                .map(category -> RedisCategory.builder()
                        .id(category.getId())
                        .name(category.getName())
                        .build())
                .toList();

        redisCategoryRepository.saveAll(redisCategories);
    }
}
