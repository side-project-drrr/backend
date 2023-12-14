package com.drrr.alarm.service.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record SubscriptionRequest(

        @NotNull
        Long id,

        @NotNull
        String endpoint,

        @NotNull
        String expirationTime,

        @NotNull
        String p256dh, // public key

        @NotNull
        String auth
) {
}
