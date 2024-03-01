package com.drrr.domain.log.repository.impl;

import static com.drrr.domain.log.entity.post.QMemberPostLog.memberPostLog;

import com.drrr.domain.log.repository.CustomMemberPostLogRepository;
import com.querydsl.core.types.dsl.DateTemplate;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CustomMemberPostLogRepositoryImpl implements CustomMemberPostLogRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<Long> findTodayUnreadRecommendPostIds(Long memberId) {
        //mysql과 h2 데이터베이스 호환되게 작성
        DateTemplate<LocalDate> date = Expressions.dateTemplate(LocalDate.class,
                "CAST({0} AS date)", memberPostLog.createdAt);
        return queryFactory
                .select(memberPostLog.postId)
                .from(memberPostLog)
                .where(memberPostLog.memberId.eq(memberId)
                        .and(memberPostLog.isRead.eq(false))
                        .and(memberPostLog.isRecommended.eq(true))
                        .and(date.eq(LocalDate.now())))
                .fetch();
    }
}
