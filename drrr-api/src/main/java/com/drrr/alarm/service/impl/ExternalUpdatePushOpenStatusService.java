package com.drrr.alarm.service.impl;

import com.drrr.infra.push.service.PushService;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class ExternalUpdatePushOpenStatusService {
    private final PushService pushService;

    public void execute(final Long memberId, final List<LocalDate> pushDates) {
        pushService.updateMemberPushOpenStatus(memberId, pushDates);
    }
}
