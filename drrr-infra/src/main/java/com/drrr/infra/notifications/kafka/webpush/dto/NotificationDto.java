package com.drrr.infra.notifications.kafka.webpush.dto;

import lombok.Builder;

@Builder
public record NotificationDto(
        String endPoint,
        String p245dh,
        String auth,
        String payload
) {
}
