package com.drrr.alarm.controller;

import com.drrr.alarm.service.impl.ExternalMemberSubscriptionService;
import com.drrr.alarm.service.impl.ExternalNotificationEmailService;
import com.drrr.alarm.service.request.SubscriptionRequest;
import com.drrr.infra.notifications.kafka.webpush.WebPushProducer;
import com.drrr.infra.push.entity.PushMessage;
import com.drrr.infra.push.entity.Subscription;
import com.drrr.infra.push.repository.SubscriptionRepository;
import com.drrr.web.jwt.util.JwtProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
//@Secured("USER")
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class PushAlarmController {
    private final ExternalNotificationEmailService externalNotificationEmailService;
    private final ExternalMemberSubscriptionService externalMemberSubscriptionService;
    private final WebPushProducer webPushProducer;
    private final JwtProvider jwtProvider;
    private final SubscriptionRepository subscriptionRepository;

    @Operation(summary = "구독 요청 API - [JWT TOKEN REQUIRED]",
            description = "호출 성공 시 사용자의 구독을 요청함")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자의 구독 신청 완료", content = @Content(schema = @Schema(implementation = HttpStatus.class)))
    })
    @PostMapping("/member/subscription")
    public void addSubscription(@RequestBody final SubscriptionRequest request) {
        final Long memberId = jwtProvider.getMemberIdFromAuthorizationToken();
        externalMemberSubscriptionService.execute(request, memberId);
    }

    @Operation(summary = "구독 취소 API - [JWT TOKEN REQUIRED]",
            description = "호출 성공 시 사용자의 구독을 취소함")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자의 구독 취소 완료", content = @Content(schema = @Schema(implementation = HttpStatus.class)))
    })
    @DeleteMapping("/member/subscription")
    public void cancelSubscription() {
        final Long memberId = jwtProvider.getMemberIdFromAuthorizationToken();
        subscriptionRepository.deleteByMemberId(memberId);
    }

    @Operation(summary = "사용하지 말 것, 개인 테스트 용 Controller",
            description = "사용하지 말 것, 개인 테스트 용 Controller")
    @PostMapping("/notifications/email")
    public void emailNotifications(@RequestBody PushMessage message) {
        Subscription subscription = Subscription.builder().memberId(1L)
                .token("eHhZn2pIpOgMl_lj0bxEFQ:APA91bHhQrGcoJpG0j6k6FK6_MgNRnXDnVfE2WJMlFtNiDpQtf4xhIHM745cZgAGqoIRSpoJA917MRea-BeaM5G0_5ptjkB-wlzRrRc_CTEP12dUCnm2c3fmkOSvfVrFQR0Sr8rFMvSz")
                .build();
        for(int i=0;i<1;i++){
            webPushProducer.sendNotifications(subscription);
        }
    }
}
