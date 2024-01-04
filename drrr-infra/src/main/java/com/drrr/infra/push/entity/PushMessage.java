package com.drrr.infra.push.entity;

import lombok.Builder;
import lombok.NonNull;

@Builder
public record PushMessage(
        @NonNull String to,
        @NonNull String subject,
        @NonNull String body
) {
}
