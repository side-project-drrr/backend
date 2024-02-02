package com.drrr.domain.techblogpost.repository.impl;

import static com.drrr.domain.log.entity.post.QMemberPostLog.memberPostLog;
import static com.drrr.domain.techblogpost.entity.QTechBlogPost.techBlogPost;
import static com.drrr.domain.techblogpost.entity.QTechBlogPostCategory.techBlogPostCategory;

import com.drrr.domain.techblogpost.dto.TechBlogPostBasicInfoDto;
import com.drrr.domain.techblogpost.entity.TechBlogPost;
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
    public Slice<TechBlogPostBasicInfoDto> findPostsByCategory(Long categoryId, Pageable pageable) {
        List<TechBlogPostBasicInfoDto> content = queryFactory.select(Projections.constructor(TechBlogPostBasicInfoDto.class
                        , techBlogPost.id
                        , techBlogPost.title
                        , techBlogPost.summary
                        , techBlogPost.techBlogCode
                        , techBlogPost.thumbnailUrl
                        , techBlogPost.viewCount
                        , techBlogPost.postLike
                        , techBlogPost.writtenAt))
                .from(techBlogPostCategory)
                .leftJoin(techBlogPostCategory.post, techBlogPost)
                .where(techBlogPostCategory.category.id.eq(categoryId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory.select(techBlogPost.id)
                .from(techBlogPostCategory)
                .leftJoin(techBlogPostCategory.post, techBlogPost)
                .where(techBlogPostCategory.category.id.eq(categoryId))
                .fetchCount();

        boolean hasNext = (pageable.getOffset() + content.size()) < total;

        return new SliceImpl<>(content, pageable, hasNext);
    }

    @Override
    public List<TechBlogPost> findTopLikePost(final int count) {
        return queryFactory.select(techBlogPost)
                .from(techBlogPost)
                .orderBy(techBlogPost.postLike.desc(), techBlogPost.writtenAt.desc())
                .limit(count)
                .fetch();
    }
}
