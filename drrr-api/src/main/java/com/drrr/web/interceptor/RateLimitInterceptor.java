package com.drrr.web.interceptor;

import com.drrr.domain.rate.service.RateLimiterService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.util.Arrays;
import java.util.Objects;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.HandlerInterceptor;

public class RateLimitInterceptor implements HandlerInterceptor {

    private final RateLimiterService rateLimiterService;
    private final int LIMIT_REQUEST_PER_MINUTE = 50;
    private final int WAIT_MINUTES = 5;
    private final Environment env;
    private final String healthCheckUrl = "/healthcheck";

    public RateLimitInterceptor(RateLimiterService rateLimiterService, Environment env) {
        this.rateLimiterService = rateLimiterService;
        this.env = env;
    }

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                             @NonNull Object handler)
            throws Exception {
        //healthCheck 도 rate limit을 적용하지 않는다.
        if (request.getRequestURI().equals(healthCheckUrl)) {
            return true;
        }

        //junit 테스트 시에는 rate limit을 적용하지 않는다.
        if (Arrays.asList(env.getActiveProfiles()).contains("test")) {
            return true;
        }

        String clientIp = request.getHeader("X-Forwarded-For");

        if (Objects.nonNull(clientIp)) {
            String api = request.getRequestURI();
            if (!rateLimiterService.isAllowed(clientIp, api, LIMIT_REQUEST_PER_MINUTE,
                    Duration.ofMinutes(WAIT_MINUTES))) {
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                return false;
            }
        }

        return true;
    }
}
