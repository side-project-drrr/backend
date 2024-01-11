package com.drrr.infra.notifications.kafka.webpush;


import com.drrr.domain.category.repository.CategoryRepository;
import com.drrr.domain.category.repository.CategoryWeightRepository;
import com.drrr.infra.notifications.kafka.webpush.dto.NotificationDto;
import com.drrr.infra.push.entity.Subscription;
import com.drrr.infra.push.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
@Slf4j
@RequiredArgsConstructor
public class WebPushProducer {
    private final SubscriptionRepository subscriptionRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final CategoryWeightRepository categoryWeightRepository;


    public void sendNotifications() {
        List<Long> memberIds = categoryWeightRepository.findMemberIdsByCategoryWeights();

        int chunkSize = 1000;

        // 천 개씩 끊어서 비동기 작업 생성
        for (int i = 0; i < memberIds.size(); i += chunkSize) {
            List<Long> subList = memberIds.subList(i, Math.min(memberIds.size(), i + chunkSize));
            CompletableFuture.supplyAsync(() ->
                    subscriptionRepository.findByMemberIdIn(subList)
            ).thenAccept((subscriptions) -> {
                subscriptions.forEach(subscription -> {
                    NotificationDto notification = NotificationDto.builder()
                            .endPoint(subscription.getEndpoint())
                            .p245dh(subscription.getP256dh())
                            .payload("좋아하실만한 기술 블로그를 추천드려요.")
                            .auth(subscription.getAuth())
                            .build();
                    this.kafkaTemplate.send("alarm-web-push", notification);
                });
            });
        }
    }
}
