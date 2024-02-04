package com.drrr.alarm.service.impl;

import com.drrr.infra.push.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class ExternalDeleteSubscriptionService {
    private final SubscriptionRepository subscriptionRepository;

    public void execute(final Long memberId) {
        subscriptionRepository.deleteByMemberId(memberId);
    }
}
