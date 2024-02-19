package com.drrr.domain.techblogpost.service;

import com.drrr.core.code.techblog.TopTechBlogType;
import com.drrr.domain.category.dto.CategoryDto;
import com.drrr.domain.category.dto.CategoryPostDto;
import com.drrr.domain.category.repository.CategoryRepository;
import com.drrr.domain.exception.DomainExceptionCode;
import com.drrr.domain.techblogpost.dto.TechBlogPostBasicInfoDto;
import com.drrr.domain.techblogpost.dto.TechBlogPostCategoryDto;
import com.drrr.domain.techblogpost.entity.TechBlogPost;
import com.drrr.domain.techblogpost.repository.TechBlogPostRepository;
import java.util.LinkedHashMap;
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
    private final CategoryRepository categoryRepository;
    private final TechBlogPostRepository techBlogPostRepository;

    public List<TechBlogPostCategoryDto> findTopPost(final int count, final TopTechBlogType type) {
        final List<Long> topPostsIds = techBlogPostRepository.findTopPost(count, type);
        if (topPostsIds.isEmpty()) {
            log.error("기술블로그를 찾을 수 없습니다.");
            throw DomainExceptionCode.TECH_BLOG_NOT_FOUND.newInstance();
        }
        return techBlogPostRepository.categorizePosts(topPostsIds);
    }

    public List<TechBlogPostCategoryDto> categorize(final List<Long> postIds) {
        return techBlogPostRepository.categorizePosts(postIds);
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

        return techBlogPostRepository.findByIdInOrderByWrittenAt(notCachedPostIds);
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
        List<TechBlogPostBasicInfoDto> posts = techBlogPostRepository.findPostsByPostIds(postIds);
        if (posts.isEmpty()) {
            log.error("기술블로그를 찾을 수 없습니다.");
            throw DomainExceptionCode.TECH_BLOG_NOT_FOUND.newInstance();
        }

        List<CategoryPostDto> eachPostCategoriesByPostIds = categoryRepository.findEachPostCategoriesByPostIds(postIds);
        if (eachPostCategoriesByPostIds.isEmpty()) {
            log.error("카테고리를 찾을 수 없습니다.");
            throw DomainExceptionCode.CATEGORY_NOT_FOUND.newInstance();
        }

        Map<Long, List<CategoryDto>> postCategories = eachPostCategoriesByPostIds.stream()
                .collect(Collectors.groupingBy(
                        CategoryPostDto::postId,
                        LinkedHashMap::new,
                        Collectors.mapping(categoryPostDto -> CategoryDto.builder()
                                        .id(categoryPostDto.categoryId())
                                        .name(categoryPostDto.name())
                                        .build(),
                                Collectors.toList())
                ));

        return posts.stream()
                .map(post -> TechBlogPostCategoryDto.builder()
                        .techBlogPostBasicInfoDto(post)
                        .categoryDto(postCategories.get(post.id()))
                        .build())
                .toList();
    }

}
