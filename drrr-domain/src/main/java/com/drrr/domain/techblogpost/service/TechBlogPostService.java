package com.drrr.domain.techblogpost.service;

import com.drrr.core.code.techblog.TopTechBlogType;
import com.drrr.domain.category.dto.CategoryDto;
import com.drrr.domain.exception.DomainExceptionCode;
import com.drrr.domain.techblogpost.dto.TechBlogPostCategoryDto;
import com.drrr.domain.techblogpost.dto.TechBlogPostContentDto;
import com.drrr.domain.techblogpost.entity.TechBlogPost;
import com.drrr.domain.techblogpost.repository.TechBlogPostRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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

    public List<TechBlogPostCategoryDto> findTopPostByType(final int count, final TopTechBlogType type) {
        final List<Long> topPostsIds = techBlogPostRepository.findTopPost(count, type);
        if (topPostsIds.isEmpty()) {
            log.error("기술블로그를 찾을 수 없습니다.");
            throw DomainExceptionCode.TECH_BLOG_NOT_FOUND.newInstance();
        }

        final Map<Long, List<CategoryDto>> postIdsCategories = techBlogPostRepository.categorizePosts(topPostsIds);

        final List<Long> postIds = postIdsCategories.keySet().stream()
                .toList();

        final List<TechBlogPostContentDto> contents = TechBlogPostContentDto.from(
                techBlogPostRepository.findPostsByPostIds(postIds));

        return TechBlogPostCategoryDto.inOrderFrom(contents, type, postIdsCategories);
    }

    public List<TechBlogPostCategoryDto> categorize(final List<Long> postIds) {
        return findPostsCategories(postIds);
    }

    public List<Long> findNotCachedTechBlogPosts(final List<Long> postsInRedis,
                                                 final List<Long> postIds) {
        //추천해줘야 할 전체 게시물 ids
        final Set<Long> postsInRedisIds = new HashSet<>(postsInRedis);

        return postIds.stream()
                .filter(id -> !postsInRedisIds.contains(id))
                .collect(Collectors.toList());

    }

    public List<TechBlogPost> findTechBlogPostsByIds(final List<Long> postIds) {
        final List<TechBlogPost> posts = techBlogPostRepository.findByIdInOrderByWrittenAt(postIds);
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

    public List<TechBlogPostCategoryDto> findPushPosts(final List<Long> postIds) {
        return findPostsCategories(postIds);
    }

    private List<TechBlogPostCategoryDto> findPostsCategories(final List<Long> postIds) {
        final Map<Long, List<CategoryDto>> postIdsCategories = techBlogPostRepository.categorizePosts(postIds);

        final List<TechBlogPostContentDto> contents = TechBlogPostContentDto.from(
                techBlogPostRepository.findPostsByPostIds(postIds));

        return TechBlogPostCategoryDto.from(contents, postIdsCategories);
    }

}
