package com.drrr.auth.service.impl;

import com.drrr.auth.payload.request.AccessTokenRequest;
import com.drrr.auth.payload.request.SignOutRequest;
import com.drrr.auth.payload.response.AccessTokenResponse;
import com.drrr.domain.auth.service.AuthenticationTokenService;
import com.drrr.domain.auth.service.AuthenticationTokenService.RegisterAuthenticationTokenDto;
import com.drrr.domain.auth.service.AuthenticationTokenService.RemoveAuthenticationTokenDto;
import com.drrr.web.jwt.util.JwtProvider;
import java.time.Instant;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IssuanceTokenService {
    private final JwtProvider tokenProvider;
    private final AuthenticationTokenService authenticationTokenService;

    public IssuanceTokenDto execute(final Long id) {
        final Instant now = Instant.now();

        final String accessToken = tokenProvider.createAccessToken(id, now);
        final String refreshToken = tokenProvider.createRefreshToken(id, now);

        authenticationTokenService.register(RegisterAuthenticationTokenDto.builder()
                .memberId(id)
                .refreshToken(refreshToken)
                .build());

        return IssuanceTokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public void negateToken(final SignOutRequest request) {
        final Long memberId = tokenProvider.extractToValueFrom(request.accessToken());
        final Long ttlMillis = tokenProvider.extractTtlMillisFromAccessToken(request.accessToken());

        authenticationTokenService.logout(memberId, request.accessToken(), ttlMillis);
    }

    public AccessTokenResponse regenerateAccessToken(final AccessTokenRequest request) {
        final Long id = tokenProvider.extractToValueFrom(request.accessToken());
        authenticationTokenService.remove(new RemoveAuthenticationTokenDto(id));

        final Instant now = Instant.now();
        final String accessToken = tokenProvider.createAccessToken(id, now);
        final String refreshToken = tokenProvider.createRefreshToken(id, now);
        authenticationTokenService.register(RegisterAuthenticationTokenDto.builder()
                .refreshToken(refreshToken)
                .memberId(id)
                .build());

        return new AccessTokenResponse(accessToken);
    }

    @Getter
    public static class IssuanceTokenDto {
        private final String accessToken;
        private final String refreshToken;

        @Builder
        private IssuanceTokenDto(final String accessToken, final String refreshToken) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
        }
    }

}

