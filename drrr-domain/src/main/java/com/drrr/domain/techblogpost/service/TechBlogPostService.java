package com.drrr.domain.techblogpost.service;

import com.drrr.domain.exception.DomainExceptionCode;
import com.drrr.domain.techblogpost.dto.TechBlogPostBasicInfoDto;
import com.drrr.domain.techblogpost.dto.TechBlogPostCategoryDto;
import com.drrr.domain.techblogpost.entity.TechBlogPost;
import com.drrr.domain.techblogpost.repository.TechBlogPostRepository;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TechBlogPostService {
    private final TechBlogPostRepository techBlogPostRepository;

    public Slice<TechBlogPostCategoryDto> findAllPostsBasic(final Pageable pageable) {
        final Slice<TechBlogPostCategoryDto> posts = techBlogPostRepository.findAllPosts(pageable);
        if (posts.isEmpty()) {
            log.error("기술블로그를 찾을 수 없습니다.");
            throw DomainExceptionCode.TECH_BLOG_NOT_FOUND.newInstance();
        }

        return posts;
    }

    public Slice<TechBlogPostCategoryDto> findPostsByCategory(final Long categoryId, final Pageable pageable) {
        final Slice<TechBlogPostCategoryDto> postsByCategory = techBlogPostRepository.findPostsByCategory(categoryId,
                pageable);
        if (postsByCategory.isEmpty()) {
            log.error("기술블로그를 찾을 수 없습니다.");
            throw DomainExceptionCode.TECH_BLOG_NOT_FOUND.newInstance();
        }
        return postsByCategory;
    }

    public List<TechBlogPostBasicInfoDto> findTopLikePost(final int count) {
        final List<TechBlogPostBasicInfoDto> topPosts = techBlogPostRepository.findTopLikePost(count);
        if (topPosts.isEmpty()) {
            log.error("기술블로그를 찾을 수 없습니다.");
            throw DomainExceptionCode.TECH_BLOG_NOT_FOUND.newInstance();
        }
        return topPosts;
    }

    public List<TechBlogPost> findNotCachedTechBlogPosts(final List<TechBlogPost> postsInRedis,
                                                         final List<Long> postIds) {
        //추천해줘야 할 전체 게시물 ids
        final Set<Long> postsInRedisIds = postsInRedis.stream()
                .map(TechBlogPost::getId)
                .collect(Collectors.toSet());

        final List<Long> notCachedPostIds = postIds.stream()
                .filter(id -> !postsInRedisIds.contains(id))
                .collect(Collectors.toList());

        return techBlogPostRepository.findByIdIn(notCachedPostIds);
    }

    public List<TechBlogPost> findTechBlogPostsByIds(final List<Long> postIds) {
        final List<TechBlogPost> posts = techBlogPostRepository.findByIdIn(postIds);
        if (posts.isEmpty()) {
            log.error("기술블로그를 찾을 수 없습니다.");
            throw DomainExceptionCode.TECH_BLOG_NOT_FOUND.newInstance();
        }
        return posts;
    }

    public TechBlogPost findTechBlogPostsById(final Long postId) {
        return techBlogPostRepository.findById(postId).orElseThrow(
                DomainExceptionCode.TECH_BLOG_NOT_FOUND::newInstance);
    }

}
