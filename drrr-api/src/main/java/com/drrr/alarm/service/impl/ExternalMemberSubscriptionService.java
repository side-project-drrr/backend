package com.drrr.alarm.service.impl;

import com.drrr.alarm.service.request.SubscriptionRequest;
import com.drrr.infra.push.entity.Subscription;
import com.drrr.infra.push.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class ExternalMemberSubscriptionService {
    private final SubscriptionService subscriptionService;

    public void execute(final SubscriptionRequest request, final Long memberId) {
        final Subscription memberSubscriptionData = Subscription.builder()
                .token(request.token())
                .memberId(memberId)
                .build();
        subscriptionService.saveMemberSubscriptionData(memberSubscriptionData);
    }

}
