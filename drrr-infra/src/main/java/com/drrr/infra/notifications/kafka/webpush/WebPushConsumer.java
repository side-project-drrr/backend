package com.drrr.infra.notifications.kafka.webpush;

import com.drrr.infra.notifications.kafka.webpush.dto.NotificationDto;
import com.drrr.infra.push.entity.PushStatus;
import com.drrr.infra.push.repository.PushStatusRepository;
import com.drrr.infra.push.repository.SubscriptionRepository;
import com.interaso.webpush.VapidKeys;
import com.interaso.webpush.WebPush.SubscriptionState;
import com.interaso.webpush.WebPushService;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@PropertySource(value = {"classpath:security-storage-infra/web-push/web-push-vapid.properties"})
public class WebPushConsumer {
    private final SubscriptionRepository subscriptionRepository;
    private final String publicKey;
    private final String privateKey;
    private final PushStatusRepository pushStatusRepository;

    public WebPushConsumer(@Value("${vapid.public.pub}") final String publicKey,
                           @Value("${vapid.private.key}") final String privateKey,
                           SubscriptionRepository subscriptionRepository, PushStatusRepository pushStatusRepository) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
        this.subscriptionRepository = subscriptionRepository;
        this.pushStatusRepository = pushStatusRepository;
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

        if (Objects.equals(sendState, SubscriptionState.ACTIVE)) {
            Long memberId = notificationDto.memberId();
            boolean hasStatus = pushStatusRepository.existsPushStatusByMemberIdAndPushDate(memberId, LocalDate.now());

            if (hasStatus) {
                return;
            }

            List<Long> postIds = pushStatusRepository.findPostIdsByMemberIdAndPushDate(memberId, LocalDate.now());

            PushStatus pushStatus = PushStatus.builder()
                    .memberId(memberId)
                    .pushDate(LocalDate.now())
                    .pushStatus(true)
                    .readStatus(false)
                    .openStatus(false)
                    .postIds(postIds)
                    .build();

            pushStatusRepository.save(pushStatus);
        }
    }
}