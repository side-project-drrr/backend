package com.drrr.auth.payload.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Builder
public record SignInResponse(
        @Schema(description = "access token", nullable = false, example = "[access token]")
        @NonNull String accessToken,
        @Schema(description = "refresh token", nullable = false, example = "[refresh token]")
        @NonNull String refreshToken) {
}
