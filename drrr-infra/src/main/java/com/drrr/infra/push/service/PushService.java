package com.drrr.infra.push.service;

import com.drrr.infra.push.dto.PushDateDto;
import com.drrr.infra.push.repository.PushStatusRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class PushService {
    private final PushStatusRepository pushStatusRepository;

    public
    List<PushDateDto> findPushesByCount(final Long memberId, final int count) {
        return pushStatusRepository.findPushDateCountAndStatusByMemberIdAndPushDate(memberId, count);
    }
    public List<Long> findMemberPushDateRange(final Long memberId, final LocalDate from, final LocalDate to) {
        return pushStatusRepository.findPostIdsByMemberIdAndPushDateRange(memberId, from, to);
    }

    public void updateMemberPushOpenStatus(final Long memberId, final List<LocalDate> pushDates) {
        pushStatusRepository.updatePushOpenStatus(memberId, pushDates);
    }

    public void updateMemberPushReadStatus(final Long memberId, final LocalDate pushDate) {
        pushStatusRepository.updatePushReadStatus(memberId, pushDate);
    }

}
