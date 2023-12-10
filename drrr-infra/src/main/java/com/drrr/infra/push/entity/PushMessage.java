package com.drrr.infra.push.entity;

import lombok.Builder;

@Builder
public record PushMessage(
        String to,
        String subject,
        String body
) {
}
