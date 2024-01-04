package com.drrr.auth.payload.request;


import jakarta.validation.constraints.NotNull;
import lombok.Builder;


@Builder
public record SignInRequest(
        @NotNull String providerId) {
}

