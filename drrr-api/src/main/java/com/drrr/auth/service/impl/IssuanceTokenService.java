package com.drrr.auth.service.impl;

import com.drrr.auth.dto.AccessTokenRequest;
import com.drrr.auth.dto.AccessTokenResponse;
import com.drrr.web.jwt.util.JwtProvider;
import com.example.drrrdomain.auth.service.AuthenticationTokenService;
import com.example.drrrdomain.auth.service.AuthenticationTokenService.RegisterAuthenticationTokenDto;
import com.example.drrrdomain.auth.service.AuthenticationTokenService.RemoveAuthenticationTokenDto;
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

    public IssuanceTokenDto execute(Long id) {
        var now = Instant.now();

        var accessToken = tokenProvider.createAccessToken(id, now);
        var refreshToken = tokenProvider.createRefreshToken(id, now);

        authenticationTokenService.register(RegisterAuthenticationTokenDto.builder()
                .memberId(id)
                .refreshToken(refreshToken)
                .build());

        return IssuanceTokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public AccessTokenResponse regenerateAccessToken(AccessTokenRequest request) {
        final Long id = tokenProvider.extractToValueFrom(request.getAccessToken());
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
        private IssuanceTokenDto(String accessToken, String refreshToken) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
        }
    }

}

