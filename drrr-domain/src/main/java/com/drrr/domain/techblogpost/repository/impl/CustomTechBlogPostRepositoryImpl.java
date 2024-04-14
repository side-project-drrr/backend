package com.drrr.domain.techblogpost.repository.impl;

import static com.drrr.domain.techblogpost.entity.QTechBlogPost.techBlogPost;
import static com.drrr.domain.techblogpost.entity.QTechBlogPostCategory.techBlogPostCategory;

import com.drrr.core.code.techblog.TopTechBlogType;
import com.drrr.domain.category.dto.CategoryDto;
import com.drrr.domain.category.dto.CategoryPostDto;
import com.drrr.domain.category.repository.CategoryRepository;
import com.drrr.domain.techblogpost.dto.TechBlogPostBasicInfo;
import com.drrr.domain.techblogpost.dto.TechBlogPostCategoryDto;
import com.drrr.domain.techblogpost.dto.TechBlogPostContentDto;
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
        final List<TechBlogPostBasicInfo> postEntities = queryFactory.select(
                        Projections.constructor(TechBlogPostBasicInfo.class
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

        final List<TechBlogPostContentDto> contents = TechBlogPostContentDto.from(postEntities);

        final Long total = queryFactory.select(techBlogPost.count())
                .from(techBlogPostCategory)
                .leftJoin(techBlogPostCategory.post, techBlogPost)
                .where(techBlogPostCategory.category.id.eq(categoryId)).fetchOne();

        return getTechBlogPostCategoryDtos(pageable, contents, total);
    }

    @Override
    public List<TechBlogPostBasicInfo> findPostsByPostIds(List<Long> postIds) {
        return queryFactory.select(
                        Projections.constructor(TechBlogPostBasicInfo.class
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
        final List<TechBlogPostBasicInfo> postEntities = queryFactory.select(
                        Projections.constructor(TechBlogPostBasicInfo.class
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

        final List<TechBlogPostContentDto> contents = TechBlogPostContentDto.from(postEntities);

        final Long total = queryFactory.select(techBlogPost.count())
                .from(techBlogPost)
                .fetchOne();

        return getTechBlogPostCategoryDtos(pageable, contents, total);
    }

    @Override
    public Slice<TechBlogPostCategoryDto> searchPostsTitleByKeyword(final String keyword, final Pageable pageable) {
        final List<TechBlogPostBasicInfo> postEntities = queryFactory.select(
                        Projections.constructor(TechBlogPostBasicInfo.class
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

        final List<TechBlogPostContentDto> contents = TechBlogPostContentDto.from(postEntities);

        final Long total = queryFactory.select(techBlogPost.count())
                .from(techBlogPost)
                .fetchOne();

        return getTechBlogPostCategoryDtos(pageable, contents, total);
    }

    //게시물의 순서는 파라미터의 postIds의 순서가 유지됨
    public Map<Long, List<CategoryDto>> categorizePosts(final List<Long> postIds) {
        List<CategoryPostDto> eachPostCategoriesByPostIds = categoryRepository.findEachPostCategoriesByPostIds(postIds);

        //key : postId, value : 해당 postId의 카테고리 ids
        return eachPostCategoriesByPostIds.stream()
                .collect(Collectors.groupingBy(
                        CategoryPostDto::postId,
                        LinkedHashMap::new,
                        Collectors.mapping(categoryPostDto -> CategoryDto.builder()
                                        .id(categoryPostDto.categoryId())
                                        .name(categoryPostDto.name())
                                        .build(),
                                Collectors.toList())
                ));
    }

    @NonNull
    private Slice<TechBlogPostCategoryDto> getTechBlogPostCategoryDtos(final Pageable pageable,
                                                                       final List<TechBlogPostContentDto> contents,
                                                                       final Long total) {
        final boolean hasNext = (pageable.getOffset() + contents.size()) < total;

        final Map<Long, List<CategoryDto>> postIdsCategories = categorizePosts(
                contents.stream()
                        .map((content) -> content.techBlogPostStaticDataDto().id())
                        .toList());

        final List<TechBlogPostCategoryDto> postCategoryDtos = contents.stream()
                .map(content -> TechBlogPostCategoryDto.builder()
                        .techBlogPostStaticDataDto(content.techBlogPostStaticDataDto())
                        .categoryDto(postIdsCategories.get(content.techBlogPostStaticDataDto().id()))
                        .techBlogPostDynamicDto(content.techBlogPostDynamicDto())
                        .build())
                .toList();

        return new SliceImpl<>(postCategoryDtos, pageable, hasNext);
    }

}
