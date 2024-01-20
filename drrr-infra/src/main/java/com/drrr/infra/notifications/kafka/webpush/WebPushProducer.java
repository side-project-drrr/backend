package com.drrr.infra.notifications.kafka.webpush;


import com.drrr.domain.category.repository.CategoryWeightRepository;
import com.drrr.infra.notifications.kafka.dto.PushMessage;
import com.drrr.infra.push.entity.Subscription;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class WebPushProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final CategoryWeightRepository categoryWeightRepository;


    public void sendNotifications(final Subscription subscription) {
        PushMessage build = PushMessage.builder()
                .token(subscription.getToken())
                .memberId(subscription.getMemberId())
                .build();
        this.kafkaTemplate.send("alarm-web-push", build);
    }
}
