package com.drrr.alarm.service.impl;

import com.drrr.infra.push.repository.PushStatusRepository;
import com.drrr.infra.push.repository.SubscriptionRepository;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class ExternalUpdatePushStatusService {
    private final PushStatusRepository pushStatusRepository;
    private final SubscriptionRepository subscriptionRepository;

    public void execute(final Long memberId, final LocalDate pushDate) {
        pushStatusRepository.updatePushStatus(memberId, pushDate);
    }
}
