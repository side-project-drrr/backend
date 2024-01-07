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

    public void insertMemberPostReadLog(final Long memberId, final Long postId) {
        //기존에 로그가 있어도 마지막 게시물을 읽은 시간을 업데이트 해야함(새벽 12시 기준으로 다시 읽으면 업데이트를 안하기 위함-조회수)
        memberPostLogRepository.findByPostIdAndMemberId(memberId, postId).ifPresentOrElse(
                existingLog -> existingLog.updateLastReadAt(LocalDateTime.now()),
                () -> memberPostLogRepository.save(MemberPostLog.builder()
                        .memberId(memberId)
                        .postId(postId)
                        .isRead(true)
                        .lastReadAt(LocalDateTime.now())
                        .isRecommended(false)
                        .build()));
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
                            .build();
                }).toList();

        memberPostLogRepository.saveAll(insertList);
    }

    public void insertMemberPostHistory(final Long memberId, final Long postId) {
        MemberPostHistory history = MemberPostHistory.builder()
                .postId(postId)
                .memberId(memberId)
                .build();
        memberPostHistoryRepository.save(history);
    }

}
