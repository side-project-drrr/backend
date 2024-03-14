package com.drrr.alarm.service.impl;

import com.drrr.infra.push.repository.PushStatusRepository;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class ExternalUpdatePushReadStatusService {
    private final PushStatusRepository pushStatusRepository;

    public void execute(final Long memberId, final LocalDate pushDate) {
        pushStatusRepository.updatePushReadStatus(memberId, pushDate);
    }
}
