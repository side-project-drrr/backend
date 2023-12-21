package com.drrr.auth.payload.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record SignInResponse(
        @Schema(description = "access token", nullable = false, example = "[access token]")
        @NotNull String accessToken,
        @Schema(description = "refresh token", nullable = false, example = "[refresh token]")
        @NotNull String refreshToken) {
}
