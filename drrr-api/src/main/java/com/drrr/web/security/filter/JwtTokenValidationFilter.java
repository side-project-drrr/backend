package com.drrr.web.security.filter;

import com.drrr.core.exception.jwt.JwtException;
import com.drrr.core.exception.jwt.JwtExceptionCode;
import com.drrr.web.jwt.util.JwtProvider;
import com.drrr.web.security.exception.JwtExpiredTokenException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class JwtTokenValidationFilter extends OncePerRequestFilter {

    private static final String TOKEN_PREFIX = "Bearer ";
    private static final String HEADER_AUTHORIZATION = "Authorization";
    private final JwtProvider jwtTokenProvider;
    private final Set<String> IgnoreUrlsSet = new HashSet<>(List.of("/actuator/prometheus"));

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
                                    final FilterChain filterChain)
            throws ServletException, IOException {

        //prometheus의 지표 수집을 위한 주기적인 request는 무시
        if (IgnoreUrlsSet.contains(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        log.info("-------------------JwtTokenValidationFilter CALL-------------------");
        log.info("-------------------request URI: " + request.getRequestURI() + "---------------");
        final String token = extractToken(request);

        if (Objects.isNull(token) || IgnoreUrlsSet.contains(request.getRequestURI())) {
            System.out.println("-----------JWT Token null-------------------");
            filterChain.doFilter(request, response);
            return;
        }

        final Long memberId = jwtTokenProvider.extractToValueFrom(token);

        try {
            if (!jwtTokenProvider.validateToken(token)) {
                throw new JwtException(JwtExceptionCode.INVALID_JWT_SIGNATURE.getCode(), JwtExceptionCode.INVALID_JWT_SIGNATURE.getMessage());
            }

            // 권한 부여
            final UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(memberId, null,
                    List.of(new SimpleGrantedAuthority("USER")));
            // Detail을 넣어줌
            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(auth);
            filterChain.doFilter(request, response);
        } catch (JwtExpiredTokenException expiredJwtException) {
            // Handle expired token exception
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write("Token expired");
            response.getWriter().flush();
            log.error("JWT 토큰이 만료되었습니다.");
            throw new JwtException(JwtExceptionCode.JWT_TOKEN_EXPIRED.getCode(), JwtExceptionCode.JWT_TOKEN_EXPIRED.getMessage());
        }
    }

    private String extractToken(final HttpServletRequest request) {
        final String token = request.getHeader(HEADER_AUTHORIZATION);

        if (token != null && token.startsWith(TOKEN_PREFIX)) {
            return token.substring(TOKEN_PREFIX.length());
        }
        return null;
    }
}
