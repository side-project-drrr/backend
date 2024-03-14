package com.drrr.alarm.service.impl;

import com.drrr.alarm.service.request.SubscriptionRequest;
import com.drrr.infra.push.entity.Subscription;
import com.drrr.infra.push.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class ExternalMemberSubscriptionService {
    private final SubscriptionService subscriptionService;


    public void execute(final SubscriptionRequest request, final Long memberId) {
        final Subscription memberSubscriptionData = Subscription.builder()
                .endpoint(request.endpoint())
                .auth(request.auth())
                .p256dh(request.p256dh())
                .expirationTime(request.expirationTime())
                .memberId(memberId)
                .build();

        subscriptionService.saveMemberSubscriptionData(memberSubscriptionData);
    }

}
