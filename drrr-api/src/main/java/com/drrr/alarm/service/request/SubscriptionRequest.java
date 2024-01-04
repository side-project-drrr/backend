package com.drrr.alarm.service.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record SubscriptionRequest(

        @NotNull
        String endpoint,

        @NotNull
        String expirationTime,

        @NotNull
        String p256dh, // public key

        // 사용자의 구독 정보 보호하고 사용자 식별하는데 사용, 서버와 클라이언트 간의 통신을 안전하게 암호화하기 위해 사용
        @NotNull
        String auth
) {
}
