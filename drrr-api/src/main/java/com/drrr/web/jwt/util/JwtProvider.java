package com.drrr.web.jwt.util;


import jakarta.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwtValidationException;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class JwtProvider {
    private static final String issuer = "DRRR-BE"; // 발급자 명칭 변경 예정
    private static final Long accessTokenExpiry = 7 * 24 * 60 * 60L; // 1주
    private static final Long refreshTokenExpiry = 14 * 24 * 60 * 60L; // 2주
    private static final String TOKEN_PREFIX = "Bearer ";
    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String HEADER_REFRESH_TOKEN = "REFRESH-TOKEN";
    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;

    public String createAccessToken(final Long id, final Instant issuanceTime) {
        final JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .issuedAt(issuanceTime)
                .expiresAt(issuanceTime.plusSeconds(accessTokenExpiry))
                .subject(String.valueOf(id))
                .claim("id", id)
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    public String createRefreshToken(final Long id, final Instant issuanceTime) {
        // UUID 혹은 id를 사용할지 고민중
        // UUID.fromString(LocalDateTime.now().toString()).toString();
        final JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .issuedAt(issuanceTime)
                .expiresAt(issuanceTime.plusSeconds(refreshTokenExpiry))
                .subject(String.valueOf(id))
                .claim("id", id)
                .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    private Map<String, Object> decode(final String token) {

        Jwt jwt = jwtDecoder.decode(token);
        if (jwt == null || jwt.getClaims() == null) {
            throw new JwtValidationException("Token is tampered", Collections.EMPTY_LIST);
        }

        Instant expiresAt = jwt.getExpiresAt();
        Instant now = Instant.now();

        assert expiresAt != null;

        if (expiresAt.isBefore(now)) {
            throw new JwtValidationException("Token is expired", Collections.EMPTY_LIST);
        }

        return jwt.getClaims();

    }

    public Long extractTtlMillisFromAccessToken(final String accessToken) {
        Instant expiresAt = jwtDecoder.decode(accessToken).getExpiresAt();
        Instant now = Instant.now();

        // 만료 시간과 현재 시간의 차이를 계산
        Duration duration = Duration.between(now, expiresAt);

        // 남은 시간을 초 단위로 반환
        return duration.toMillis();
    }

    public Long extractToValueFrom(final String token) {
        return Long.parseLong(this.decode(token).get("id").toString());
    }

    public String extractAccessToken(final HttpServletRequest request) {
        final String token = request.getHeader(HEADER_AUTHORIZATION);

        if (!Objects.isNull(token) && token.startsWith(TOKEN_PREFIX)) {
            return token.substring(TOKEN_PREFIX.length());
        }
        return null;
    }

    public String extractRefreshToken(final HttpServletRequest request) {
        final String token = request.getHeader(HEADER_REFRESH_TOKEN);

        if (!Objects.isNull(token) && token.startsWith(TOKEN_PREFIX)) {
            return token.substring(TOKEN_PREFIX.length());
        }
        return null;
    }

    public boolean validateToken(final String token) {
        try {
            this.decode(token);
            return true;
        } catch (BadJwtException e) {
            log.error("유효하지 않은 JWT 토큰");
            return false;
        }
    }
}