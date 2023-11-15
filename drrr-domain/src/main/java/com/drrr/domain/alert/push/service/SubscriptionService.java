package com.drrr.domain.alert.push.service;


import com.drrr.domain.alert.push.entity.Subscription;
import com.drrr.domain.alert.push.repository.SubscriptionRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@RequiredArgsConstructor
@Transactional
@Service
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    public void saveMemberSubscriptionData(final Subscription subscription){
        subscriptionRepository.save(subscription);
    }

    public void deleteMemberSubscriptionData(final Long memberId){
        subscriptionRepository.deleteByMemberId(memberId);
    }


}
