package com.drrr.domain.techblogpost.repository.impl;

import static com.drrr.domain.category.entity.QCategory.category;
import static com.drrr.domain.techblogpost.entity.QTemporalTechBlogPost.temporalTechBlogPost;
import static com.drrr.domain.techblogpost.entity.QTemporalTechPostTag.temporalTechPostTag;

import com.drrr.core.date.DateRangeBound;
import com.drrr.domain.techblogpost.entity.TemporalTechBlogPost;
import com.drrr.domain.techblogpost.repository.CustomTemporalTechBlogPostRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;


@RequiredArgsConstructor
@Repository
public class TemporalTechBlogPostRepositoryImpl implements CustomTemporalTechBlogPostRepository {
    private final JPAQueryFactory queryFactory;

    private BooleanExpression betweenBy(final DateRangeBound rangeBound) {
        if (rangeBound == null) {
            return null;
        }
        return temporalTechBlogPost.crawledDate.between(rangeBound.getStartDate(), rangeBound.getLastDate());
    }

    private BooleanExpression isRegistrationCompleted(final Boolean assignTagCompleted) {
        if (assignTagCompleted == null) {
            return null;
        }
        return temporalTechBlogPost.registrationCompleted.eq(assignTagCompleted);
    }

    @Override
    public List<TemporalTechBlogPost> findBy(final DateRangeBound rangeBound,
                                             final Boolean assignTagCompleted,
                                             final Pageable pageable
    ) {
        return queryFactory.select(temporalTechBlogPost)
                .from(temporalTechBlogPost)
                .leftJoin(temporalTechBlogPost.temporalTechPostTags, temporalTechPostTag)
                .fetchJoin()
                .leftJoin(temporalTechPostTag.category, category).fetchJoin()
                .where(betweenBy(rangeBound), isRegistrationCompleted(assignTagCompleted))
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetch();
    }

    @Override
    public List<TemporalTechBlogPost> findBy(final DateRangeBound rangeBound,
                                             final Boolean assignTagCompleted
    ) {
        return queryFactory.select(temporalTechBlogPost)
                .from(temporalTechBlogPost)
                .leftJoin(temporalTechBlogPost.temporalTechPostTags, temporalTechPostTag).fetchJoin()
                .leftJoin(temporalTechPostTag.category, category).fetchJoin()
                .where(betweenBy(rangeBound),
                        isRegistrationCompleted(assignTagCompleted)
                ).fetch();
    }

}
