package com.drrr.auth.payload.response;


import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Builder
public record SignUpResponse(@NonNull String accessToken, @NonNull String refreshToken) {
}
