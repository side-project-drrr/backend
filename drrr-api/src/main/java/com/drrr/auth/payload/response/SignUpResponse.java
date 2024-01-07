package com.drrr.auth.payload.response;


import com.drrr.auth.service.impl.IssuanceTokenService.IssuanceTokenDto;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record SignUpResponse(
        @NotNull String accessToken,
        @NotNull String refreshToken) {
    public static SignUpResponse from(IssuanceTokenDto issuanceTokenDto) {
        return SignUpResponse.builder()
                .accessToken(issuanceTokenDto.getAccessToken())
                .refreshToken(issuanceTokenDto.getRefreshToken())
                .build();
    }
}
