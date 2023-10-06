package com.drrr.web.security.filter;

import com.drrr.web.jwt.util.JwtProvider;
import com.drrr.web.security.exception.JwtExpiredTokenException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class JwtTokenValidationFilter extends OncePerRequestFilter {

    private static final String TOKEN_PREFIX = "Bearer ";
    private static final String HEADER_AUTHORIZATION = "Authorization";
    private final JwtProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        log.info("-------------------JwtTokenValidationFilter CALL-------------------");
        log.info("-------------------request URI: " + request.getRequestURI() + "---------------");
        var token = extractToken(request);
        if (Objects.isNull(token)) {
            System.out.println("-----------JWT null-------------------");
            filterChain.doFilter(request, response);
            return;
        }

        var memberId = jwtTokenProvider.extractToValueFrom(token);

        try {
            if (!jwtTokenProvider.validateToken(token)) {
                throw new JwtExpiredTokenException("Access token has expired");
            }

            // 권한 부여
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(memberId, null,
                    List.of(new SimpleGrantedAuthority("USER")));
            // Detail을 넣어줌
            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(auth);
            log.info("[+] Token in SecurityContextHolder");
            filterChain.doFilter(request, response);
        } catch (JwtExpiredTokenException expiredJwtException) {
            // Handle expired token exception
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write("Token expired");
            response.getWriter().flush();
        }
    }

    private String extractToken(HttpServletRequest request) {
        String token = request.getHeader(HEADER_AUTHORIZATION);

        if (token != null && token.startsWith(TOKEN_PREFIX)) {
            return token.substring(TOKEN_PREFIX.length());
        }
        return null;
    }
}
