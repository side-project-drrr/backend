package com.drrr.infra.notifications.kafka.email;

import com.drrr.domain.alert.push.entity.PushMessage;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;


@Service
@Slf4j
@RequiredArgsConstructor
public class EmailConsumer {


    private final JavaMailSender javaMailSender;

    @KafkaListener(topics = "alarm-email", groupId = "group_email", containerFactory = "kafkaEmailListenerContainerFactory")
    public void consumePush(final PushMessage message, Acknowledgment ack) {
        MimeMessage mimeMessage = buildMessage(message);
        javaMailSender.send(mimeMessage);
        ack.acknowledge();
    }

    @KafkaListener(topics = "verification-email", groupId = "group_email", containerFactory = "kafkaEmailListenerContainerFactory")
    public void consumeVerification(final PushMessage message, Acknowledgment ack) {
        MimeMessage mimeMessage = buildMessage(message);
        javaMailSender.send(mimeMessage);
        ack.acknowledge();
    }
    
    private MimeMessage buildMessage(final PushMessage message){
        try{
            final MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            final MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            mimeMessageHelper.setTo(message.to()); // 메일 수신자
            mimeMessageHelper.setSubject(message.subject()); // 메일 제목
            mimeMessageHelper.setText(message.body(), true); // 메일 본문 내용, HTML 여부
            return mimeMessage;
        }catch(MessagingException e){
            log.error("[Message Push Failed Error]");
            log.error("Message Send Failed To Member Email Address : " + message.to());
            throw new RuntimeException(e);
        }
    }

}
