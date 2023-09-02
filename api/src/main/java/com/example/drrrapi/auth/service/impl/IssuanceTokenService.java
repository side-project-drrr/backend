package com.example.drrrapi.auth.service.impl;

import com.example.drrrapi.auth.dto.AccessTokenRequest;
import com.example.drrrapi.auth.dto.AccessTokenResponse;
import com.example.drrrapi.auth.entity.AuthenticationToken;
import com.example.drrrapi.auth.infrastructure.redis.RedisAuthenticationTokenRepository;
import com.example.drrrapi.web.jwt.util.JwtProvider;
import java.time.Instant;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IssuanceTokenService {
    private final JwtProvider tokenProvider;
    private final RedisAuthenticationTokenRepository authenticationTokenRepository;

    public IssuanceTokenDto execute(Long id) {
        var now = Instant.now();

        var accessToken = tokenProvider.createAccessToken(id, now);
        var refreshToken = tokenProvider.createRefreshToken(id, now);

        authenticationTokenRepository.save(AuthenticationToken.builder()
                .memberId(id)
                .refreshToken(refreshToken)
                .build()
        );

        return IssuanceTokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public AccessTokenResponse regenerateAccessToken(AccessTokenRequest request) {
        var id = tokenProvider.extractToValueFrom(request.getAccessToken());
        var authenticationToken = authenticationTokenRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자 입니다."));

        authenticationTokenRepository.delete(authenticationToken);
        var now = Instant.now();

        var accessToken = tokenProvider.createAccessToken(id, now);
        var refreshToken = tokenProvider.createRefreshToken(id, now);

        authenticationTokenRepository.save(AuthenticationToken.builder()
                .refreshToken(refreshToken)
                .memberId(id)
                .build()
        );
        return new AccessTokenResponse(accessToken);
    }
}

@Getter
class IssuanceTokenDto {
    private final String accessToken;
    private final String refreshToken;

    @Builder
    private IssuanceTokenDto(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
