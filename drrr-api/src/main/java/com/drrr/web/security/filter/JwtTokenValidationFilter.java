package com.drrr.web.security.filter;

import com.drrr.error.ErrorResponse;
import com.drrr.web.exception.ApiExceptionCode;
import com.drrr.web.jwt.util.JwtProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class JwtTokenValidationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtTokenProvider;
    private final Set<String> IgnoreUrlsSet = new HashSet<>(List.of("/actuator/prometheus", "/healthcheck"));

    @Override
    protected void doFilterInternal(final HttpServletRequest request, @NotNull final HttpServletResponse response,
                                    @NotNull final FilterChain filterChain)
            throws ServletException, IOException {

        //prometheus의 지표 수집을 위한 주기적인 request는 무시
        if (IgnoreUrlsSet.contains(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        log.info("-------------------JwtTokenValidationFilter CALL-------------------");
        log.info("-------------------request URI: " + request.getRequestURI() + "---------------");
        final String token = jwtTokenProvider.extractToken(request);

        if (Objects.isNull(token)) {

            log.info("-----------JWT Token null-------------------");
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final Long memberId = jwtTokenProvider.extractToValueFrom(token);
            jwtTokenProvider.validateToken(token);
            final UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(memberId, null,
                    List.of(new SimpleGrantedAuthority("USER")));
            // Detail을 넣어줌
            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(auth);
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            returnErrorResponse(response, ApiExceptionCode.JWT_UNAUTHORIZED);
        }
    }

    private void returnErrorResponse(
            HttpServletResponse response,
            ApiExceptionCode errorCode
    ) {
        ObjectMapper objectMapper = new ObjectMapper();
        response.setStatus(errorCode.getCode());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();
        try {
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
