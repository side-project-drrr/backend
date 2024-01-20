package com.drrr.alarm.service.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record SubscriptionRequest(

        @NotNull
        String token
) {
}
