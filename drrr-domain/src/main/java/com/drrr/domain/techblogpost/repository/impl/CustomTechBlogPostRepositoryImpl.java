package com.drrr.domain.techblogpost.repository.impl;

import static com.drrr.domain.log.entity.post.QMemberPostLog.memberPostLog;
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

    public List<Long> recommendRemain(final Long memberId, final int remainedPostCount) {
        //로그 테이블에서 사용자가 읽은 적이 없는 최신 기술블로그를 추천
        return queryFactory.select(techBlogPost.id)
                .from(techBlogPost)
                .leftJoin(memberPostLog)
                .on(
                        memberPostLog.postId.eq(techBlogPost.id),
                        memberPostLog.memberId.eq(memberId)
                )
                .where(memberPostLog.id.isNull())
                .orderBy(techBlogPost.writtenAt.desc())
                .limit(remainedPostCount)
                .fetch();
    }

    @Override
    public Slice<TechBlogPostCategoryDto> findPostsByCategory(Long categoryId, Pageable pageable) {
        List<TechBlogPostBasicInfoDto> content = queryFactory.select(
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
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory.select(techBlogPost.count())
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
        List<TechBlogPostBasicInfoDto> content = queryFactory.select(
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
                .orderBy(techBlogPost.writtenAt.desc(), techBlogPost.postLike.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory.select(techBlogPost.count())
                .from(techBlogPost)
                .fetchOne();

        boolean hasNext = (pageable.getOffset() + content.size()) < total;

        return getTechBlogPostCategoryDtos(pageable, content, total);
    }

    @Override
    public Slice<TechBlogPostCategoryDto> searchPostsTitleByWord(final String index, final Pageable pageable) {
        List<TechBlogPostBasicInfoDto> content = queryFactory.select(
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
                .where(techBlogPost.title.like("%" + index + "%"))
                .orderBy(techBlogPost.writtenAt.desc(), techBlogPost.postLike.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory.select(techBlogPost.count())
                .from(techBlogPost)
                .fetchOne();

        return getTechBlogPostCategoryDtos(pageable, content, total);
    }

    public List<TechBlogPostCategoryDto> categorizePosts(final List<Long> postIds) {
        List<CategoryPostDto> eachPostCategoriesByPostIds = categoryRepository.findEachPostCategoriesByPostIds(postIds);

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
        boolean hasNext = (pageable.getOffset() + content.size()) < total;

        List<TechBlogPostCategoryDto> postCategoryDtos = categorizePosts(
                content.stream().map(TechBlogPostBasicInfoDto::id).toList());

        return new SliceImpl<>(postCategoryDtos, pageable, hasNext);
    }

}
