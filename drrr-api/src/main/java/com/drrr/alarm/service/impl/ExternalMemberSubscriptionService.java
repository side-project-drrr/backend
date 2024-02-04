package com.drrr.alarm.service.impl;

import com.drrr.alarm.service.request.SubscriptionRequest;
import com.drrr.domain.techblogpost.dto.TechBlogPostCategoryDto;
import com.drrr.domain.techblogpost.service.TechBlogPostService;
import com.drrr.infra.push.entity.Subscription;
import com.drrr.infra.push.repository.PushPostRepository;
import com.drrr.infra.push.service.SubscriptionService;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ExternalMemberSubscriptionService {
    private final SubscriptionService subscriptionService;
    private final TechBlogPostService techBlogPostService;
    private final PushPostRepository pushPostRepository;

    public List<TechBlogPostCategoryDto> execute(final Long memberId, final LocalDate pushDate) {
        List<Long> postIds = pushPostRepository.findPostIdByMemberIdAndPushDate(memberId, pushDate);
        return techBlogPostService.findPushPosts(postIds);
    }

    @Transactional
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
