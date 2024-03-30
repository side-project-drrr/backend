package com.drrr.alarm.service.impl;

import com.drrr.alarm.service.request.PushDatesRequest;
import com.drrr.infra.push.repository.PushStatusRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class ExternalPushStatusUpdateService {
    private final PushStatusRepository pushStatusRepository;
    public void updateOpenStatus(final Long memberId, final PushDatesRequest request) {
        pushStatusRepository.updatePushOpenStatus(memberId, request.pushDates());
    }
    public void updateReadStatus(final Long memberId, final LocalDate pushDate) {
        pushStatusRepository.updatePushReadStatus(memberId, pushDate);
    }
}
