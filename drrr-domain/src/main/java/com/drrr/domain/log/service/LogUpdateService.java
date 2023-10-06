package com.drrr.domain.log.service;

import static com.drrr.domain.log.entity.post.QMemberPostLog.memberPostLog;

import com.drrr.domain.log.entity.history.MemberPostHistory;
import com.drrr.domain.log.entity.post.MemberPostLog;
import com.drrr.domain.log.repository.MemberPostHistoryRepository;
import com.drrr.domain.log.repository.MemberPostLogRepository;
import com.drrr.domain.techblogpost.entity.TechBlogPost;
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
@Transactional
public class LogUpdateService {
    private final JPAQueryFactory queryFactory;
    private final MemberPostHistoryRepository memberPostHistoryRepository;
    private final MemberPostLogRepository memberPostLogRepository;

    public void insertMemberPostReadLog(Long memberId, Long postId) {
        memberPostLogRepository.findByPostId(postId)
                .orElseGet(() ->  memberPostLogRepository.save(MemberPostLog.builder()
                        .memberId(memberId)
                        .postId(postId)
                        .isRead(true)
                        .isRecommended(false)
                        .build()));
    }
    public void updateMemberPostRecommendLog(Long memberId, List<TechBlogPost> posts) {
        List<Long> postIds = posts.stream()
                .map(post -> post.getId())
                .toList();

        List<MemberPostLog> logs = queryFactory.selectFrom(memberPostLog)
                .where(postIdsInOrEq(postIds), memberPostLog.memberId.eq(memberId))
                .fetch();

        //읽었던 게시물이 아니라면, postIds의 개수와 로그로 조회된 값이 다를 때 새로 insert
        if (logs.size() != postIds.size()) {
            insertMemberPostLog(memberId, postIds, logs);
            return;
        }

        logs.stream().forEach(log->log.updateRecommendStatus());

    }
    public void insertMemberPostLog(Long memberId, List<Long> postIds, List<MemberPostLog> logs) {
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
                            .isRecommended(true)
                            .build();
                }).toList();

        memberPostLogRepository.saveAll(insertList);
    }

    public void insertMemberPostHistory(Long memberId, Long postId) {
        MemberPostHistory history = MemberPostHistory.builder()
                .postId(postId)
                .memberId(memberId)
                .build();
        memberPostHistoryRepository.save(history);
    }

    private BooleanExpression postIdsInOrEq(List<Long> postIds) {
        return postIds.size() == 1 ? memberPostLog.postId.eq(postIds.get(0))
                : memberPostLog.postId.in(postIds);
    }
}
