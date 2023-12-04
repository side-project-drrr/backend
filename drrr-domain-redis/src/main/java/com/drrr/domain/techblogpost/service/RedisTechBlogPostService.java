package com.drrr.domain.techblogpost.service;

import com.drrr.domain.techblogpost.entity.RedisTechBlogPost;
import com.drrr.domain.techblogpost.entity.TechBlogPost;
import com.drrr.domain.techblogpost.repository.RedisTechBlogPostRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class RedisTechBlogPostService {
    private final RedisTechBlogPostRepository redisTechBlogPostRepository;

    public List<TechBlogPost> findPostsByIds(List<Long> postIds){
        final List<RedisTechBlogPost> redisTechBlogPosts = (List<RedisTechBlogPost>)redisTechBlogPostRepository.findAllById(postIds);
        return redisTechBlogPosts.stream()
                .map((entity) -> entity.getTechBlogPost())
                .toList();
    }

    public void savePostsInRedis(List<TechBlogPost> posts){
        final List<RedisTechBlogPost> redisTechBlogPosts = posts.stream()
                        .map((entity) -> RedisTechBlogPost.builder()
                                .id(entity.getId())
                                .techBlogPost(entity)
                                .build())
                        .toList();

        redisTechBlogPostRepository.saveAll(redisTechBlogPosts);
    }
}
