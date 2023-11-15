package com.drrr.domain.alert.push.entity;

import lombok.Builder;

@Builder
public record PushMessage(
        String to,
        String subject,
        String body
) {
}
