package com.drrr.domain.techblogpost.repository.impl;

import static com.drrr.domain.category.entity.QCategory.category;
import static com.drrr.domain.techblogpost.entity.QTemporalTechBlogPost.temporalTechBlogPost;
import static com.drrr.domain.techblogpost.entity.QTemporalTechPostTag.temporalTechPostTag;

import com.drrr.core.code.techblog.TechBlogCode;
import com.drrr.core.date.DateRangeBound;
import com.drrr.domain.techblogpost.entity.TemporalTechBlogPost;
import com.drrr.domain.techblogpost.repository.CustomTemporalTechBlogPostRepository;
import com.drrr.domain.techblogpost.service.SearchTemporaryTechBlogPostService.SearchTemporaryTechBlogPostDto;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;


@RequiredArgsConstructor
@Repository
public class TemporalTechBlogPostRepositoryImpl implements CustomTemporalTechBlogPostRepository {
    private final JPAQueryFactory queryFactory;

    private <T> BooleanExpression generateNullCondition(T value, Supplier<BooleanExpression> supplier) {
        if (Objects.isNull(value)) {
            return null;
        }
        return supplier.get();
    }

    private BooleanExpression betweenBy(final DateRangeBound rangeBound) {
        return generateNullCondition(rangeBound, () -> temporalTechBlogPost.crawledDate.between(rangeBound.getStartDate(), rangeBound.getLastDate()));
    }

    private BooleanExpression isRegistrationCompleted(final Boolean assignTagCompleted) {
        return generateNullCondition(assignTagCompleted, () -> temporalTechBlogPost.registrationCompleted.eq(assignTagCompleted));
    }

    private BooleanExpression startsWithTitle(final String title) {
        return generateNullCondition(title, () -> temporalTechBlogPost.title.startsWith(title));
    }

    private BooleanExpression matchTechBlogCode(final TechBlogCode techBlogCode) {
        return generateNullCondition(techBlogCode, () -> temporalTechBlogPost.techBlogCode.eq(techBlogCode));
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

    @Override
    public Page<TemporalTechBlogPost> findBy(SearchTemporaryTechBlogPostDto searchTemporaryTechBlogPostDto, Pageable pageable) {
        final var techBlogPosts = queryFactory.select(temporalTechBlogPost)
                .from(temporalTechBlogPost)
                .leftJoin(temporalTechBlogPost.temporalTechPostTags, temporalTechPostTag).fetchJoin()
                .leftJoin(temporalTechPostTag.category, category).fetchJoin()
                .where(
                        betweenBy(searchTemporaryTechBlogPostDto.dateRangeBound()),
                        isRegistrationCompleted(searchTemporaryTechBlogPostDto.assignTagCompleted()),
                        startsWithTitle(searchTemporaryTechBlogPostDto.title()),
                        matchTechBlogCode(searchTemporaryTechBlogPostDto.code())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        final var countQuery = queryFactory.select(temporalTechBlogPost.count())
                .from(temporalTechBlogPost)
                .where(
                        betweenBy(searchTemporaryTechBlogPostDto.dateRangeBound()),
                        isRegistrationCompleted(searchTemporaryTechBlogPostDto.assignTagCompleted()),
                        startsWithTitle(searchTemporaryTechBlogPostDto.title()),
                        matchTechBlogCode(searchTemporaryTechBlogPostDto.code())
                );

        return PageableExecutionUtils.getPage(techBlogPosts, pageable, countQuery::fetchOne);
    }
}
