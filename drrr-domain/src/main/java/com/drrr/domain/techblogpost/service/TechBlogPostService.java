package com.drrr.domain.techblogpost.service;

import static com.drrr.domain.techblogpost.entity.QTechBlogPost.techBlogPost;
import static com.drrr.domain.techblogpost.entity.QTechBlogPostCategory.techBlogPostCategory;

import com.drrr.core.exception.techblog.TechBlogExceptionCode;
import com.drrr.domain.techblogpost.dto.TechBlogPostOuterDto;
import com.drrr.domain.techblogpost.entity.TechBlogPost;
import com.drrr.domain.techblogpost.repository.TechBlogPostRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
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
    public List<TechBlogPostOuterDto> findAllPostsOuter() {
        final List<TechBlogPost> posts = techBlogPostRepository.findAll();
        if (posts.isEmpty()) {
            log.error("기술블로그를 찾을 수 없습니다.");
            throw TechBlogExceptionCode.TECH_BLOG_NOT_FOUND.newInstance();
        }
        return TechBlogPostOuterDto.from(posts);
    }

    public List<TechBlogPost> findPostsByCategory(final Long postId) {
        final List<TechBlogPost> posts = techBlogPostRepository.findPostsByCategory(postId);
        if (posts.isEmpty()) {
            log.error("기술블로그를 찾을 수 없습니다.");
            throw TechBlogExceptionCode.TECH_BLOG_NOT_FOUND.newInstance();
        }
        return posts;
    }

    public List<TechBlogPostOuterDto> findTopLikePost(final int count) {
        final List<TechBlogPost> topPosts = techBlogPostRepository.findTopLikePost(count);
        if (topPosts.isEmpty()) {
            log.error("기술블로그를 찾을 수 없습니다.");
            throw TechBlogExceptionCode.TECH_BLOG_NOT_FOUND.newInstance();
        }
        return TechBlogPostOuterDto.from(topPosts);
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
            throw TechBlogExceptionCode.TECH_BLOG_NOT_FOUND.newInstance();
        }
        return posts;
    }

    public TechBlogPost findTechBlogPostsById(final Long postId) {
        return techBlogPostRepository.findById(1L).orElseThrow(
                TechBlogExceptionCode.TECH_BLOG_NOT_FOUND::newInstance);
    }

}
