package com.drrr.infra.notifications.kafka.webpush;


import com.drrr.infra.notifications.kafka.dto.PushMessage;
import com.drrr.infra.push.repository.SubscriptionRepository;
import com.google.firebase.FirebaseException;
import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebPushConsumer {
    private static int count =1;
    private final SubscriptionRepository subscriptionRepository;

    @KafkaListener(topics = "alarm-web-push", groupId = "group_web_push", containerFactory = "kafkaWebPushListenerContainerFactory")
    public void consume(final PushMessage payload) throws FirebaseMessagingException {

        Notification notification = Notification.builder()
                .setTitle("DR--R--R--")
                .setBody("count -> "+(count))
                .build();

        Message message2 = Message.builder()
                .setNotification(notification)
                .setToken(payload.token())
                .build();

        try{
            String response = FirebaseMessaging.getInstance().send(message2);
            System.out.println("Successfully sent message: " + response+" count -> "+(count++));
        }catch(FirebaseMessagingException fe){
            //토큰이 유효하지 않은 경우엔 토큰 삭제 후 카프카에 exception를 던지지 않음
            if(fe.getMessagingErrorCode().equals(MessagingErrorCode.UNREGISTERED)){
                subscriptionRepository.deleteByMemberId(payload.memberId());
                return;
            }
            log.error("Firebase Messaging Exception Occurred -> "+fe.getMessage());
            throw fe;
        }
    }
}
