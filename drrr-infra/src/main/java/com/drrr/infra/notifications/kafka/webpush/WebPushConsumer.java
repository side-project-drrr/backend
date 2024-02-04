package com.drrr.infra.notifications.kafka.webpush;

import com.drrr.infra.notifications.kafka.webpush.dto.NotificationDto;
import com.drrr.infra.push.repository.SubscriptionRepository;
import com.interaso.webpush.VapidKeys;
import com.interaso.webpush.WebPush.SubscriptionState;
import com.interaso.webpush.WebPushService;
import java.util.Base64;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@PropertySource(value = {"classpath:security-storage-infra/notification/web-push/web-push-vapid.properties"})
public class WebPushConsumer {
    private final SubscriptionRepository subscriptionRepository;
    private final String publicKey;
    private final String privateKey;

    public WebPushConsumer(@Value("${vapid.public.pub}") final String publicKey,
                           @Value("${vapid.private.key}") final String privateKey,
                           SubscriptionRepository subscriptionRepository) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
        this.subscriptionRepository = subscriptionRepository;
    }

    @KafkaListener(topics = "alarm-web-push", groupId = "group_web_push", containerFactory = "kafkaWebPushListenerContainerFactory")
    public void consume(final NotificationDto notificationDto) {
        VapidKeys vapidKeys = VapidKeys.fromUncompressedBytes(
                publicKey,
                privateKey);

        WebPushService webPushService = new WebPushService(
                "mailto:youngyou1324@naver.com",
                vapidKeys);

        byte[] p256 = Base64.getUrlDecoder().decode(notificationDto.p256dh());
        byte[] auth = Base64.getUrlDecoder().decode(notificationDto.auth());

        SubscriptionState sendState = webPushService.send(
                notificationDto.payload().getBytes(),
                notificationDto.endpoint(),
                p256,
                auth,
                null,
                null,
                null
        );

        if (Objects.equals(sendState, SubscriptionState.EXPIRED)) {
            subscriptionRepository.deleteById(notificationDto.id());
        }
    }
}