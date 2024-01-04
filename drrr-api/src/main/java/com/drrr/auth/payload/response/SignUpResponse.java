package com.drrr.auth.payload.response;


import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record SignUpResponse(@NotNull String accessToken, @NotNull String refreshToken) {
}
