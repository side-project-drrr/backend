package com.drrr.alarm.controller;

import com.drrr.alarm.service.impl.ExternalMemberSubscriptionService;
import com.drrr.alarm.service.impl.ExternalNotificationEmailService;
import com.drrr.alarm.service.request.SubscriptionRequest;
import com.drrr.domain.alert.push.entity.PushMessage;
import com.drrr.infra.notifications.kafka.webpush.WebPushProducer;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
public class PushAlarmController {
    private final ExternalNotificationEmailService externalNotificationEmailService;
    private final ExternalMemberSubscriptionService externalMemberSubscriptionService;
    private final WebPushProducer webPushProducer;

    @PreAuthorize("hasAuthority('USER')")
    @PostMapping("/subscription")
    public ResponseEntity<?> addSubscription(@RequestBody final SubscriptionRequest request) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long memberId = Long.valueOf(authentication.getName());
        externalMemberSubscriptionService.executeSubscription(request, memberId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @PreAuthorize("hasAuthority('USER')")
    @DeleteMapping("/subscription/{memberId}")
    public ResponseEntity<?> cancelSubscription(@NonNull @PathVariable(name = "memberId") final Long memberId) {
        externalMemberSubscriptionService.executeUnsubscription(memberId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('USER')")
    @PostMapping("/notifications/email")
    public ResponseEntity<String> emailNotifications(@RequestBody PushMessage message){
       // webPushProducer.sendNotifications();
        externalNotificationEmailService.execute(message);
        return ResponseEntity.ok("Email requests sent to Kafka");
    }
}
