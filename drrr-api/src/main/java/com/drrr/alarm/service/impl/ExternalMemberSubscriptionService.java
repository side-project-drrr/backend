package com.drrr.alarm.service.impl;

import com.drrr.alarm.service.request.SubscriptionRequest;

import com.drrr.domain.alert.push.entity.Subscription;
import com.drrr.domain.alert.push.service.SubscriptionService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class ExternalMemberSubscriptionService {
    private final SubscriptionService subscriptionService;
    public void executeSubscription(final SubscriptionRequest request, final Long memberId) {
        Subscription memberSubscriptionData = Subscription.builder()
                .endpoint(request.endpoint())
                .auth(request.auth())
                .p256dh(request.p256dh())
                .expirationTime(request.expirationTime())
                .memberId(memberId)
                .build();

        subscriptionService.saveMemberSubscriptionData(memberSubscriptionData);
    }

    public void executeUnsubscription(final Long memberId) {
        subscriptionService.deleteMemberSubscriptionData(memberId);
    }

}
