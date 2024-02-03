package com.drrr.domain.techblogpost.repository.impl;

import static com.drrr.domain.log.entity.post.QMemberPostLog.memberPostLog;
import static com.drrr.domain.techblogpost.entity.QTechBlogPost.techBlogPost;
import static com.drrr.domain.techblogpost.entity.QTechBlogPostCategory.techBlogPostCategory;

import com.drrr.domain.category.dto.CategoryDto;
import com.drrr.domain.category.repository.CategoryRepository;
import com.drrr.domain.techblogpost.dto.TechBlogPostBasicInfoDto;
import com.drrr.domain.techblogpost.dto.TechBlogPostCategoryDto;
import com.drrr.domain.techblogpost.repository.CustomTechBlogPostRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
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
                                , techBlogPost.writtenAt)
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

        boolean hasNext = (pageable.getOffset() + content.size()) < total;

        List<TechBlogPostCategoryDto> techBlogPostCategoryDtos = content.stream()
                .map(post -> {
                    List<CategoryDto> categoriesByPostId = categoryRepository.findCategoriesByPostId(post.id());
                    return TechBlogPostCategoryDto.builder()
                            .techBlogPostBasicInfoDto(post)
                            .categoryDto(categoriesByPostId)
                            .build();
                })
                .toList();

        return new SliceImpl<>(techBlogPostCategoryDtos, pageable, hasNext);
    }

    @Override
    public List<TechBlogPostBasicInfoDto> findTopLikePost(final int count) {
        return queryFactory.select(
                        Projections.constructor(TechBlogPostBasicInfoDto.class
                                , techBlogPost.id
                                , techBlogPost.title
                                , techBlogPost.summary
                                , techBlogPost.techBlogCode
                                , techBlogPost.thumbnailUrl
                                , techBlogPost.viewCount
                                , techBlogPost.postLike
                                , techBlogPost.writtenAt)
                )
                .from(techBlogPost)
                .orderBy(techBlogPost.postLike.desc(), techBlogPost.writtenAt.desc())
                .limit(count)
                .fetch();
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
                                , techBlogPost.writtenAt)
                )
                .from(techBlogPost)
                .orderBy(techBlogPost.postLike.desc(), techBlogPost.writtenAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory.select(techBlogPost.count())
                .from(techBlogPost)
                .fetchOne();

        boolean hasNext = (pageable.getOffset() + content.size()) < total;

        List<TechBlogPostCategoryDto> techBlogPostCategoryDtos = content.stream()
                .map(post -> {
                    List<CategoryDto> categoriesByPostId = categoryRepository.findCategoriesByPostId(post.id());
                    return TechBlogPostCategoryDto.builder()
                            .techBlogPostBasicInfoDto(post)
                            .categoryDto(categoriesByPostId)
                            .build();
                })
                .toList();

        return new SliceImpl<>(techBlogPostCategoryDtos, pageable, hasNext);
    }

}
