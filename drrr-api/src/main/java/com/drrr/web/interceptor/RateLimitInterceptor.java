package com.drrr.web.interceptor;

import com.drrr.domain.rate.service.RateLimiterService;
import com.nimbusds.jose.Header;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.util.Arrays;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;

public class RateLimitInterceptor implements HandlerInterceptor {

    private final RateLimiterService rateLimiterService;
    private final int LIMIT_REQUEST_PER_MINUTE = 300;
    private final int WAIT_MINUTES = 5;
    private final Environment env;

    public RateLimitInterceptor(RateLimiterService rateLimiterService, Environment env) {
        this.rateLimiterService = rateLimiterService;
        this.env = env;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if (Arrays.asList(env.getActiveProfiles()).contains("test")) {
            return true;
        }

        String clientIp = request.getRemoteAddr(); // Assuming the client id is passed in header
        String api = request.getRequestURI().startsWith("/api") ? "api" : request.getRequestURI();

        if (!rateLimiterService.isAllowed(clientIp, api, LIMIT_REQUEST_PER_MINUTE, Duration.ofMinutes(WAIT_MINUTES))) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            return false;
        }

        return true;
    }
}
