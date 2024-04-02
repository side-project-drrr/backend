package com.drrr.domain.techblogpost.repository.custom;

import static com.drrr.domain.log.entity.post.QMemberPostLog.memberPostLog;
import static com.drrr.domain.techblogpost.entity.QTechBlogPost.techBlogPost;
import static com.drrr.domain.techblogpost.entity.QTechBlogPostCategory.techBlogPostCategory;

import com.drrr.domain.category.dto.ExtractedPostCategoryDto;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class CustomTechBlogPostCategoryRepositoryImpl implements CustomTechBlogPostCategoryRepository {
    private final JPAQueryFactory queryFactory;


    @Override
    public List<ExtractedPostCategoryDto> findPostsByCategoryIdsNotInLog(final List<Long> categoryIds,
                                                                         final Long memberId) {

        return queryFactory.select(
                        Projections.constructor(ExtractedPostCategoryDto.class, techBlogPost.id,
                                techBlogPostCategory.category.id))
                .from(techBlogPostCategory)
                .innerJoin(techBlogPostCategory.post, techBlogPost)
                .leftJoin(memberPostLog)
                .on(memberPostLog.postId.eq(techBlogPost.id).and(memberPostLog.memberId.eq(memberId)))
                .where(techBlogPostCategory.category.id.in(categoryIds)
                        .and(memberPostLog.id.isNull()))
                .orderBy(techBlogPost.writtenAt.desc())
                .fetch();
    }

}
