package com.drrr.domain.techblogpost.service;

import com.drrr.core.exception.techblog.TechBlogExceptionCode;
import com.drrr.domain.techblogpost.entity.TechBlogPost;
import com.drrr.domain.techblogpost.repository.TechBlogPostRepository;
import com.drrr.domain.techblogpost.repository.custom.CustomTechBlogPostCategoryRepository;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TechBlogPostService {
    private final TechBlogPostRepository techBlogPostRepository;
    private final CustomTechBlogPostCategoryRepository customTechBlogPostCategoryRepository;

    public List<TechBlogPost> findAllPosts() {
        List<TechBlogPost> posts = techBlogPostRepository.findAll();
        if (posts.size() == 0) {
            log.error("기술블로그를 찾을 수 없습니다.");
            throw TechBlogExceptionCode.TECH_BLOG_NOT_FOUND.newInstance();
        }
        return posts;
    }

    public List<TechBlogPost> findPostsByCategory(Long postId) {
        List<TechBlogPost> posts = customTechBlogPostCategoryRepository.getPostsByCategory(postId);
        if (posts.size() == 0) {
            log.error("기술블로그를 찾을 수 없습니다.");
            throw TechBlogExceptionCode.TECH_BLOG_NOT_FOUND.newInstance();
        }
        return posts;
    }

    public List<TechBlogPost> findNotCachedTechBlogPosts(final List<TechBlogPost> postsInRedis,
                                                         final List<Long> postIds) {
        //추천해줘야 할 전체 게시물 ids
        Set<Long> postsInRedisIds = postsInRedis.stream()
                .map(TechBlogPost::getId)
                .collect(Collectors.toSet());

        List<Long> notCachedPostIds = postIds.stream()
                .filter(id -> !postsInRedisIds.contains(id))
                .collect(Collectors.toList());

        return techBlogPostRepository.findByIdIn(notCachedPostIds);
    }

    public List<TechBlogPost> findTechBlogPostsByIds(List<Long> postIds) {
        List<TechBlogPost> posts = techBlogPostRepository.findByIdIn(postIds);
        if (posts.size() == 0) {
            log.error("기술블로그를 찾을 수 없습니다.");
            throw TechBlogExceptionCode.TECH_BLOG_NOT_FOUND.newInstance();
        }
        return posts;
    }

}
