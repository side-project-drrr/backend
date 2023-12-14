package com.drrr.infra.push.service;


import com.drrr.infra.push.entity.Subscription;
import com.drrr.infra.push.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@RequiredArgsConstructor
@Transactional
@Service
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;

    public void saveMemberSubscriptionData(final Subscription subscription) {
        subscriptionRepository.save(subscription);
    }

    public void deleteMemberSubscriptionData(final Long memberId) {
        subscriptionRepository.deleteByMemberId(memberId);
    }


}
