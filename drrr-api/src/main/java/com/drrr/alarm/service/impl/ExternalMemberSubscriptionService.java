package com.drrr.alarm.service.impl;

import com.drrr.alarm.service.request.SubscriptionRequest;
import com.drrr.domain.techblogpost.dto.TechBlogPostCategoryDto;
import com.drrr.domain.techblogpost.service.TechBlogPostService;
import com.drrr.infra.push.entity.Subscription;
import com.drrr.infra.push.repository.PushStatusRepository;
import com.drrr.infra.push.service.SubscriptionService;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class ExternalMemberSubscriptionService {
    private final SubscriptionService subscriptionService;
    private final TechBlogPostService techBlogPostService;

    private final PushStatusRepository pushStatusRepository;

    public List<TechBlogPostCategoryDto> execute(final Long memberId, final LocalDate pushDate) {
        //푸시 상태 변경
        pushStatusRepository.updatePushStatus(memberId, pushDate);

        final List<Long> postIds = pushStatusRepository.findPostIdsByMemberIdAndPushDate(memberId, pushDate);
        return techBlogPostService.findPushPosts(postIds);
    }

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
