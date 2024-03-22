package com.drrr.domain.log.service;

import com.drrr.domain.log.entity.history.MemberPostHistory;
import com.drrr.domain.log.entity.post.MemberPostLog;
import com.drrr.domain.log.repository.MemberPostHistoryRepository;
import com.drrr.domain.log.repository.MemberPostLogRepository;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
@Transactional
public class LogUpdateService {
    private final MemberPostHistoryRepository memberPostHistoryRepository;
    private final MemberPostLogRepository memberPostLogRepository;

    public void insertMemberLogAndHistory(final Long memberId, final Long postId) {
        MemberPostLog memberPostLog = memberPostLogRepository.findByPostIdAndMemberId(memberId, postId)
                .orElseGet(() -> memberPostLogRepository.save(MemberPostLog.builder()
                        .memberId(memberId)
                        .postId(postId)
                        .lastReadAt(LocalDateTime.now())
                        .isRead(true)
                        .isRecommended(false)
                        .build()));
        
        memberPostLog.updateReadStatus();

        MemberPostHistory history = MemberPostHistory.builder()
                .postId(postId)
                .memberId(memberId)
                .build();
        memberPostHistoryRepository.save(history);
    }


    public void insertTodayMemberPostRecommendLog(final Long memberId, final List<Long> postIds) {
        final List<Long> loggedPostIds = memberPostLogRepository.findTodayUnreadRecommendPostIds(memberId);
        final Set<Long> postIdSet = new HashSet<>(postIds);
        Set<Long> loggedPostIdSet = new HashSet<>(loggedPostIds);

        postIdSet.removeAll(loggedPostIdSet);

        List<Long> toBeLoggedPostIds = postIdSet.stream().toList();

        //로그 데이터 INSERT
        insertMemberPostLog(memberId, toBeLoggedPostIds);
    }

    public void insertMemberPostLog(final Long memberId, final List<Long> postIds) {
        final Set<Long> postIdSet = new HashSet<>(postIds);

        //추천 상태를 true로 설정, 읽음 상태는 false인 이유는 추천할 때 읽지 않은 것을 기준으로 추천을 해주기 때문
        List<MemberPostLog> insertList = postIdSet.stream()
                .map(postId -> {
                    return MemberPostLog.builder()
                            .postId(postId)
                            .memberId(memberId)
                            .isRead(false)
                            .isRecommended(true)
                            .lastReadAt(LocalDateTime.now())
                            .build();
                }).toList();

        memberPostLogRepository.saveAll(insertList);
    }
}
