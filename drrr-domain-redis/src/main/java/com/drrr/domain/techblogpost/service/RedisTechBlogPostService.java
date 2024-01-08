package com.drrr.domain.techblogpost.service;

import com.drrr.domain.techblogpost.entity.RedisCategoryTechBlogPost;
import com.drrr.domain.techblogpost.entity.RedisTechBlogPost;
import com.drrr.domain.techblogpost.entity.TechBlogPost;
import com.drrr.domain.techblogpost.repository.RedisCategoryTechBlogPostRepository;
import com.drrr.domain.techblogpost.repository.RedisTechBlogPostRepository;
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
public class RedisTechBlogPostService {
    private final RedisTechBlogPostRepository redisTechBlogPostRepository;
    private final RedisCategoryTechBlogPostRepository redisCategoryTechBlogPostRepository;
    private final RedisTemplate<String, RedisTechBlogPost> redisTechBlogPostTemplate;

    public List<TechBlogPost> findPostsByIdsInRedis(final List<Long> postIds) {
        //redis repository에서는 찾고자하는 데이터가 없으면 빈 리스트 대신 null를 반환함
        final List<String> keys = postIds.stream()
                .map(Object::toString)
                .toList();

        final List<RedisTechBlogPost> redisTechBlogPosts = redisTechBlogPostTemplate.opsForValue().multiGet(keys);

        return redisTechBlogPosts.stream()
                .filter(Objects::nonNull)
                .map(RedisTechBlogPost::getTechBlogPost)
                .toList();
    }

    public TechBlogPost findPostByIdInRedis(final Long postId) {
        //redis repository에서는 찾고자하는 데이터가 없으면 빈 리스트 대신 null를 반환함
        return redisTechBlogPostRepository.findById(postId).map(RedisTechBlogPost::getTechBlogPost)
                .orElse(null);
    }

    public List<TechBlogPost> findPostsByCategoryIdInRedis(final Long categoryId) {
        final Optional<RedisCategoryTechBlogPost> redisCategoryTechBlogPosts = redisCategoryTechBlogPostRepository.findById(
                categoryId);

        return redisCategoryTechBlogPosts.map(
                redisCategoryTechBlogPost -> redisCategoryTechBlogPost.getTechBlogPost().stream()
                        .filter(Objects::nonNull)
                        .toList()).orElse(null);

    }

    public void savePostsInRedis(final List<TechBlogPost> posts) {
        final List<RedisTechBlogPost> redisTechBlogPosts = posts.stream()
                .map((entity) -> RedisTechBlogPost.builder()
                        .id(entity.getId())
                        .techBlogPost(entity)
                        .build())
                .toList();

        redisTechBlogPostRepository.saveAll(redisTechBlogPosts);
    }

    public void saveCategoryPostsInRedis(final Long categoryId, final List<TechBlogPost> posts) {
        final RedisCategoryTechBlogPost redisPosts = RedisCategoryTechBlogPost.builder()
                .id(categoryId)
                .techBlogPost(posts)
                .build();

        redisCategoryTechBlogPostRepository.save(redisPosts);
    }
}
