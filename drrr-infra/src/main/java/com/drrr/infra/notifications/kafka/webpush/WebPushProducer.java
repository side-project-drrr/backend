package com.drrr.infra.notifications.kafka.webpush;


import com.drrr.domain.alert.push.entity.Subscription;
import com.drrr.domain.alert.push.repository.SubscriptionRepository;
import com.drrr.infra.notifications.kafka.webpush.dto.NotificationDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WebPushProducer {
    private final SubscriptionRepository subscriptionRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    public void sendNotifications() {
        List<Subscription> subscriptions = subscriptionRepository.findAll();
        if (subscriptions.size() == 0) {
            throw new IllegalArgumentException("subscription elements is null");
        }

        subscriptions.stream().forEach(subscription -> {
            NotificationDto notification = NotificationDto.builder()
                    .endPoint(subscription.getEndpoint())
                    .p245dh(subscription.getP256dh())
                    .payload("웹 푸시 내용이 여기에 들어갑니다.")
                    .auth(subscription.getAuth())
                    .build();
            this.kafkaTemplate.send("alarm-web-push", notification);
        });

    }
}
