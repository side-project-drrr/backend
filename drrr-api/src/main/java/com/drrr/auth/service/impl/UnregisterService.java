package com.drrr.auth.service.impl;

import com.drrr.domain.member.service.UnregisterMemberService;
import com.drrr.infra.push.service.SubscriptionService;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UnregisterService {
    private final UnregisterMemberService unregisterMemberService;
    private final SubscriptionService subscriptionService;

    public void execute(final Long memberId) {
        //CompletableFuture
        CompletableFuture.runAsync(() -> unregisterMemberService.unregisterMember(memberId))
                .thenRun(() -> subscriptionService.deleteMemberSubscriptionData(memberId))
                .join();
    }
}
