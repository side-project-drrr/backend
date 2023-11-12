package com.drrr.auth.payload.request;


import lombok.Builder;
import org.springframework.lang.NonNull;


@Builder
public record SignInRequest(
        @NonNull String providerId) {
}

