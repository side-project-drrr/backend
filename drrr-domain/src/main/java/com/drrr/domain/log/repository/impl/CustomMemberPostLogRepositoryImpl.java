package com.drrr.domain.log.repository.impl;

import static com.drrr.domain.log.entity.post.QMemberPostLog.memberPostLog;

import com.drrr.domain.log.entity.post.MemberPostLog;
import com.drrr.domain.log.repository.CustomMemberPostLogRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CustomMemberPostLogRepositoryImpl implements CustomMemberPostLogRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<MemberPostLog> findByMemberPostLog(Long memberId, List<Long> postIds) {
        return queryFactory
                .selectFrom(memberPostLog)
                .where(postIdsInOrEq(postIds), memberPostLog.memberId.eq(memberId))
                .fetch();
    }

    private BooleanExpression postIdsInOrEq(final List<Long> postIds) {
        return postIds.size() == 1 ? memberPostLog.postId.eq(postIds.get(0))
                : memberPostLog.postId.in(postIds);
    }
}
