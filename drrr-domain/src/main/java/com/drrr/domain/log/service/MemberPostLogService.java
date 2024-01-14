package com.drrr.domain.log.service;

import com.drrr.domain.log.entity.post.MemberPostLog;
import com.drrr.domain.log.repository.MemberPostLogRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberPostLogService {
    private final MemberPostLogRepository memberPostLogRepository;

    public boolean hasDayPassedAfterReading(final Long memberId, final Long postId) {
        Optional<MemberPostLog> byPostIdAndMemberId = memberPostLogRepository.findByPostIdAndMemberId(memberId, postId);

        if (byPostIdAndMemberId.isEmpty()) {
            return false;
        }

        LocalDateTime lastReadAt = byPostIdAndMemberId.get().getLastReadAt();
        LocalDateTime startOfNextDayOfLastRead = lastReadAt.plusDays(1).toLocalDate().atStartOfDay();

        return lastReadAt.isAfter(startOfNextDayOfLastRead);

    }
}
