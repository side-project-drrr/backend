package com.drrr.infra.notifications.kafka.webpush;


import com.drrr.domain.category.repository.CategoryWeightRepository;
import com.drrr.infra.notifications.kafka.webpush.dto.NotificationDto;
import com.drrr.infra.push.entity.Subscription;
import com.drrr.infra.push.repository.SubscriptionRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class WebPushProducer {
    private final SubscriptionRepository subscriptionRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final CategoryWeightRepository categoryWeightRepository;


    public void sendNotifications() {
        final List<Subscription> subscriptions = subscriptionRepository.findAll();

        subscriptions.stream().forEach(subscription -> {
            NotificationDto notification = NotificationDto.builder()
                    .id(subscription.getId())
                    .endpoint(subscription.getEndpoint())
                    .p256dh(subscription.getP256dh())
                    .payload("DRRR에서 새로 업데이트된 게시물을 만나보세요!")
                    .auth(subscription.getAuth())
                    .memberId(subscription.getMemberId())
                    .build();
            this.kafkaTemplate.send("alarm-web-push", notification);
        });
    }
}