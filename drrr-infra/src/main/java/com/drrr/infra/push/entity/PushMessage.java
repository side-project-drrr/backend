package com.drrr.infra.push.entity;

import lombok.Builder;
import org.antlr.v4.runtime.misc.NotNull;

@Builder
public record PushMessage(
        @NotNull String to,
        @NotNull String subject,
        @NotNull String body
) {
}
