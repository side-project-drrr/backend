package com.drrr.infra.notifications.kafka.webpush.dto;

import lombok.Builder;

@Builder
public record NotificationDto(
        String endpoint,
        String p256dh,
        String auth,
        String payload,
        Long memberId
) {
}