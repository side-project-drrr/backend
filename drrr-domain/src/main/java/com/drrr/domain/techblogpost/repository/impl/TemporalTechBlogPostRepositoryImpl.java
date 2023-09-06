package com.drrr.domain.techblogpost.repository.impl;

import static com.drrr.domain.techblogpost.entity.QTemporalTechBlogPost.temporalTechBlogPost;
import static com.drrr.domain.techblogpost.entity.QTemporalTechPostTag.temporalTechPostTag;

import com.drrr.core.date.DateRangeBound;
import com.drrr.domain.techblogpost.entity.TemporalTechBlogPost;
import com.drrr.domain.techblogpost.repository.CustomTemporalTechBlogPostRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;


@RequiredArgsConstructor
@Repository
public class TemporalTechBlogPostRepositoryImpl implements CustomTemporalTechBlogPostRepository {
    private final JPAQueryFactory queryFactory;


    @Override
    public List<TemporalTechBlogPost> findBy(DateRangeBound rangeBound) {
        return queryFactory.select(temporalTechBlogPost)
                .from(temporalTechBlogPost)
                .leftJoin(temporalTechBlogPost.temporalTechPostTags, temporalTechPostTag)
                .fetchJoin()
                .where(temporalTechBlogPost.crawledDate.between(rangeBound.getStartDate(), rangeBound.getLastDate()))
                .fetch();

    }


}
