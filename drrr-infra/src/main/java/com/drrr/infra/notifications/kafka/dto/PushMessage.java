package com.drrr.infra.notifications.kafka.dto;

import lombok.Builder;

@Builder
public record PushMessage(
        String token,
        Long memberId
) {
}
