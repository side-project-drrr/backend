package com.drrr.auth.payload.request;

import com.drrr.auth.service.impl.IssuanceTokenService.IssuanceTokenDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record SignOutRequest(
        @Schema(description = "access token", nullable = false, example = "[access token]")
        @NotNull String accessToken,
        @Schema(description = "refresh token", nullable = false, example = "[refresh token]")
        @NotNull String refreshToken) {
    public static SignOutRequest from(IssuanceTokenDto issuanceTokenDto) {
        return SignOutRequest.builder()
                .accessToken(issuanceTokenDto.getAccessToken())
                .refreshToken(issuanceTokenDto.getRefreshToken())
                .build();
    }
}
