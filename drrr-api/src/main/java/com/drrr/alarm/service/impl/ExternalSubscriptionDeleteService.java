package com.drrr.alarm.service.impl;

import com.drrr.infra.push.repository.PushStatusRepository;
import com.drrr.infra.push.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ExternalSubscriptionDeleteService {
    private final SubscriptionRepository subscriptionRepository;

    public void deleteSubscription(final Long memberId) {
        subscriptionRepository.deleteByMemberId(memberId);
    }
}
