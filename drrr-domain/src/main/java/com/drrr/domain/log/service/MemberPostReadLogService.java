package com.drrr.domain.log.service;


import static com.drrr.domain.log.entity.post.QMemberPostLog.memberPostLog;

import com.drrr.domain.log.entity.post.MemberPostLog;
import com.drrr.domain.log.repository.MemberTechBlogPostRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.impl.JPAUpdateClause;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MemberPostReadLogService {
    private final MemberTechBlogPostRepository memberTechBlogPostRepository;
    private final JPAQueryFactory queryFactory;
    @PersistenceContext
    private EntityManager em;

    @Transactional
    public void updateMemberPostLog(Long memberId, List<Long> postIds, boolean isRead, boolean isRecommended) {
        //사용자가 읽었던 게시물인지 아닌지 검사
        List<MemberPostLog> logs = queryFactory.selectFrom(memberPostLog)
                .where(postIdsInOrEq(postIds), memberPostLog.memberId.eq(memberId))
                .fetch();

        //읽었던 게시물이 아니라면, postIds의 개수와 로그로 조회된 값이 다를 때 새로 insert
        if (logs.size() != postIds.size()) {
            insertMemberPostLog(memberId, postIds, logs);
        }

        JPAUpdateClause updateClause = new JPAUpdateClause(em, memberPostLog);


        if (isRead) {
            updateClause.set(memberPostLog.isRead, true);
        }

        if (isRecommended) {
            updateClause.set(memberPostLog.isRecommended, true);
        }

        updateClause.where(postIdsInOrEq(postIds)).execute();
    }


    private void insertMemberPostLog(Long memberId, List<Long> postIds, List<MemberPostLog> logs) {
        Set<Long> postIdSet = new HashSet<>(postIds);

        //postIdSet에 log 테이블에 저장이 안되어 있는 postId만 남기기
        for (MemberPostLog log : logs) {
            postIdSet.remove(log.getPostId());
        }

        List<MemberPostLog> insertList = postIdSet.stream()
                .map(postId -> {
                    return MemberPostLog.builder()
                            .postId(postId)
                            .memberId(memberId)
                            .isRead(false)
                            .isRecommended(false)
                            .build();
                }).toList();

        memberTechBlogPostRepository.saveAll(insertList);
    }

    private BooleanExpression postIdsInOrEq(List<Long> postIds) {
        return postIds.size() == 1 ? memberPostLog.postId.eq(postIds.get(0))
                : memberPostLog.postId.in(postIds);
    }
}
