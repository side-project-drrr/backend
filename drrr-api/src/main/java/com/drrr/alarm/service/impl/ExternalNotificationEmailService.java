package com.drrr.alarm.service.impl;


import com.drrr.infra.notifications.kafka.email.EmailProducer;
import com.drrr.infra.push.entity.PushMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ExternalNotificationEmailService {
    private final EmailProducer emailProducer;
    public void execute(final PushMessage pushMessage) {
        emailProducer.sendRecommendationMessage();
    }
}
