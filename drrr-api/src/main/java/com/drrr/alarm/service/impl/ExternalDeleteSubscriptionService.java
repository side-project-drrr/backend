package com.drrr.alarm.service.impl;

import com.drrr.infra.push.repository.SubscriptionRepository;
import com.drrr.infra.push.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class ExternalDeleteSubscriptionService {
    private final SubscriptionService subscriptionService;

    public void execute(final Long memberId) {
        subscriptionService.deleteMemberSubscriptionData(memberId);
    }
}
