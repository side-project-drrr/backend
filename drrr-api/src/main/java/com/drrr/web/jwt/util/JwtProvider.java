package com.drrr.web.jwt.util;


import com.drrr.core.exception.jwt.JwtExceptionCode;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwtValidationException;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class JwtProvider {
    private static final String issuer = "TEAM-SAIDA-BE"; // 발급자 명칭 변경 예정
    private static final Long accessTokenExpiry = 7 * 24 * 60 * 60L; // 1주
    private static final Long refreshTokenExpiry = 14 * 24 * 60 * 60L; // 2주
    private static final String TOKEN_PREFIX = "Bearer ";
    private static final String HEADER_AUTHORIZATION = "Authorization";
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
        return jwtDecoder.decode(token).getClaims();
    }


    public Long extractToValueFrom(final String token) {
        try {
            return Long.parseLong(this.decode(token).get("id").toString());
        } catch (JwtValidationException ex) {
            throw JwtExceptionCode.JWT_UNAUTHORIZED.newInstance();
        }
    }

    public String extractToken(final HttpServletRequest request) {
        final String token = request.getHeader(HEADER_AUTHORIZATION);

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

    public Long getMemberIdFromAuthorizationToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return Long.valueOf(authentication.getName());
    }

}