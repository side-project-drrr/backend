package com.drrr.domain.log.service;

import com.drrr.domain.log.entity.post.MemberPostLog;
import com.drrr.domain.log.repository.MemberPostLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberPostLogService {
    private final MemberPostLogRepository memberPostLogRepository;
    public boolean hasDayPassedAfterReading(final Long memberId, final Long postId) {
        return memberPostLogRepository.findByPostIdAndMemberId(memberId, postId)
                .map((memberPostLog)->{
                    LocalDateTime lastReadAt = memberPostLog.getLastReadAt();
                    LocalDateTime startOfNextDayOfLastRead = lastReadAt.plusDays(1).toLocalDate().atStartOfDay();

                    return lastReadAt.isAfter(startOfNextDayOfLastRead);
                })
                .orElse(false);
    }
    
}
