package com.drrr.alarm.service.impl;


import com.drrr.domain.alert.push.entity.PushMessage;
import com.drrr.infra.notifications.kafka.email.EmailProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ExternalNotificationEmailService {
    private final EmailProducer emailProducer;
    public void execute(final PushMessage pushMessage) {
        emailProducer.sendMessage();
    }
}
