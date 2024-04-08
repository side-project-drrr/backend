package com.drrr.domain.log.repository.impl;

import static com.drrr.domain.log.entity.post.QMemberPostLog.memberPostLog;

import com.drrr.domain.log.repository.CustomMemberPostLogRepository;
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
        return queryFactory
                .select(memberPostLog.postId)
                .from(memberPostLog)
                .where(memberPostLog.memberId.eq(memberId)
                        .and(memberPostLog.isRead.eq(false))
                        .and(memberPostLog.isRecommended.eq(true))
                        .and(memberPostLog.recommendedAt.eq(LocalDate.now())))
                .fetch();
    }
}
