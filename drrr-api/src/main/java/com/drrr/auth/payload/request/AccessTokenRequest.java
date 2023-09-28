package com.drrr.auth.payload.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class AccessTokenRequest {
    @Schema(description = "유효한 refresh token", nullable = false, example = "JWT token")
    private String refreshToken;
    @Schema(description = "유효기간이 만료되거나 만료되기 직전인 access token", nullable = false, example = "JWT token")
    private String accessToken;
}