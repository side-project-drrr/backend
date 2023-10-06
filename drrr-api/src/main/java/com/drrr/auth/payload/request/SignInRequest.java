package com.drrr.auth.payload.request;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.lang.NonNull;


@Builder
public record SignInRequest(
        @NonNull String accessToken,
        @NonNull String provider) {
}

