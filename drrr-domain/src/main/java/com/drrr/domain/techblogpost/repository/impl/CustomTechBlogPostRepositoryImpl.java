package com.drrr.domain.techblogpost.repository.impl;

import static com.drrr.domain.techblogpost.entity.QTechBlogPost.techBlogPost;
import static com.drrr.domain.techblogpost.entity.QTechBlogPostCategory.techBlogPostCategory;

import com.drrr.core.code.techblog.TopTechBlogType;
import com.drrr.domain.category.dto.CategoryDto;
import com.drrr.domain.category.dto.CategoryPostDto;
import com.drrr.domain.category.repository.CategoryRepository;
import com.drrr.domain.techblogpost.dto.TechBlogPostBasicInfoDto;
import com.drrr.domain.techblogpost.dto.TechBlogPostCategoryDto;
import com.drrr.domain.techblogpost.repository.CustomTechBlogPostRepository;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class CustomTechBlogPostRepositoryImpl implements CustomTechBlogPostRepository {
    private final CategoryRepository categoryRepository;
    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<TechBlogPostCategoryDto> findPostsByCategory(Long categoryId, Pageable pageable) {
        final List<TechBlogPostBasicInfoDto> content = queryFactory.select(
                        Projections.constructor(TechBlogPostBasicInfoDto.class
                                , techBlogPost.id
                                , techBlogPost.title
                                , techBlogPost.summary
                                , techBlogPost.techBlogCode
                                , techBlogPost.thumbnailUrl
                                , techBlogPost.viewCount
                                , techBlogPost.postLike
                                , techBlogPost.writtenAt
                                , techBlogPost.url)
                )
                .from(techBlogPostCategory)
                .leftJoin(techBlogPostCategory.post, techBlogPost)
                .where(techBlogPostCategory.category.id.eq(categoryId))
                .orderBy(techBlogPost.writtenAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        final Long total = queryFactory.select(techBlogPost.count())
                .from(techBlogPostCategory)
                .leftJoin(techBlogPostCategory.post, techBlogPost)
                .where(techBlogPostCategory.category.id.eq(categoryId)).fetchOne();

        return getTechBlogPostCategoryDtos(pageable, content, total);
    }

    @Override
    public List<TechBlogPostBasicInfoDto> findPostsByPostIds(List<Long> postIds) {
        return queryFactory.select(
                        Projections.constructor(TechBlogPostBasicInfoDto.class
                                , techBlogPost.id
                                , techBlogPost.title
                                , techBlogPost.summary
                                , techBlogPost.techBlogCode
                                , techBlogPost.thumbnailUrl
                                , techBlogPost.viewCount
                                , techBlogPost.postLike
                                , techBlogPost.writtenAt
                                , techBlogPost.url)
                )
                .from(techBlogPost)
                .where(techBlogPost.id.in(postIds))
                .orderBy(techBlogPost.writtenAt.desc())
                .fetch();
    }

    @Override
    public List<Long> findTopPost(final int count, final TopTechBlogType type) {

        return queryFactory.select(techBlogPost.id)
                .from(techBlogPost)
                .orderBy(topBlogTypeCondition(type), techBlogPost.writtenAt.desc())
                .limit(count)
                .fetch();
    }

    private OrderSpecifier<Integer> topBlogTypeCondition(final TopTechBlogType type) {
        if (type.equals(TopTechBlogType.VIEWS)) {
            return techBlogPost.viewCount.desc();
        }
        return techBlogPost.postLike.desc();
    }

    @Override
    public Slice<TechBlogPostCategoryDto> findAllPosts(Pageable pageable) {
        final List<TechBlogPostBasicInfoDto> content = queryFactory.select(
                        Projections.constructor(TechBlogPostBasicInfoDto.class
                                , techBlogPost.id
                                , techBlogPost.title
                                , techBlogPost.summary
                                , techBlogPost.techBlogCode
                                , techBlogPost.thumbnailUrl
                                , techBlogPost.viewCount
                                , techBlogPost.postLike
                                , techBlogPost.writtenAt
                                , techBlogPost.url
                        )
                )
                .from(techBlogPost)
                .orderBy(techBlogPost.writtenAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        final Long total = queryFactory.select(techBlogPost.count())
                .from(techBlogPost)
                .fetchOne();

        return getTechBlogPostCategoryDtos(pageable, content, total);
    }

    @Override
    public Slice<TechBlogPostCategoryDto> searchPostsTitleByKeyword(final String keyword, final Pageable pageable) {
        final List<TechBlogPostBasicInfoDto> content = queryFactory.select(
                        Projections.constructor(TechBlogPostBasicInfoDto.class
                                , techBlogPost.id
                                , techBlogPost.title
                                , techBlogPost.summary
                                , techBlogPost.techBlogCode
                                , techBlogPost.thumbnailUrl
                                , techBlogPost.viewCount
                                , techBlogPost.postLike
                                , techBlogPost.writtenAt
                                , techBlogPost.url
                        )
                )
                .from(techBlogPost)
                .where(techBlogPost.title.toUpperCase().like("%" + keyword.toUpperCase() + "%"))
                .orderBy(techBlogPost.writtenAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        final Long total = queryFactory.select(techBlogPost.count())
                .from(techBlogPost)
                .fetchOne();

        return getTechBlogPostCategoryDtos(pageable, content, total);
    }

    //게시물의 순서는 파라미터의 postIds의 순서가 유지됨
    public List<TechBlogPostCategoryDto> categorizePosts(final List<Long> postIds) {
        List<CategoryPostDto> eachPostCategoriesByPostIds = categoryRepository.findEachPostCategoriesByPostIds(postIds);

        final Map<Long, List<CategoryDto>> postCategories = eachPostCategoriesByPostIds.stream()
                .collect(Collectors.groupingBy(
                        CategoryPostDto::postId,
                        LinkedHashMap::new,
                        Collectors.mapping(categoryPostDto -> CategoryDto.builder()
                                        .id(categoryPostDto.categoryId())
                                        .name(categoryPostDto.name())
                                        .build(),
                                Collectors.toList())
                ));

        return findPostsByPostIds(postIds).stream()
                .map(post -> TechBlogPostCategoryDto.builder()
                        .techBlogPostBasicInfoDto(post)
                        .categoryDto(postCategories.get(post.id()))
                        .build())
                .toList();
    }

    @NonNull
    private Slice<TechBlogPostCategoryDto> getTechBlogPostCategoryDtos(final Pageable pageable,
                                                                       final List<TechBlogPostBasicInfoDto> content,
                                                                       final Long total) {
        final boolean hasNext = (pageable.getOffset() + content.size()) < total;

        final List<TechBlogPostCategoryDto> postCategoryDtos = categorizePosts(
                content.stream().map(TechBlogPostBasicInfoDto::id).toList());

        return new SliceImpl<>(postCategoryDtos, pageable, hasNext);
    }

}
