package com.drrr.domain.techblogpost.service;

import com.drrr.domain.techblogpost.entity.RedisCategoryTechBlogPost;
import com.drrr.domain.techblogpost.entity.RedisTechBlogPost;
import com.drrr.domain.techblogpost.entity.TechBlogPost;
import com.drrr.domain.techblogpost.repository.RedisCategoryTechBlogPostRepository;
import com.drrr.domain.techblogpost.repository.RedisTechBlogPostRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class RedisTechBlogPostService {
    private final RedisTechBlogPostRepository redisTechBlogPostRepository;
    private final RedisCategoryTechBlogPostRepository redisCategoryTechBlogPostRepository;

    public List<TechBlogPost> findPostsByIdsInRedis(final List<Long> postIds) {
        final List<RedisTechBlogPost> redisTechBlogPosts = (List<RedisTechBlogPost>) redisTechBlogPostRepository.findAll();
        return redisTechBlogPosts.stream()
                .map((entity) -> entity.getTechBlogPost())
                .toList();
    }

    public List<TechBlogPost> findPostsByCategoryIdInRedis(final Long categoryId) {
        final RedisCategoryTechBlogPost redisCategoryTechBlogPosts = redisCategoryTechBlogPostRepository.findById(
                categoryId).get();

        return redisCategoryTechBlogPosts.getTechBlogPost();
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
