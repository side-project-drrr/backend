package com.drrr.web.interceptor;

import com.drrr.domain.rate.service.RateLimiterService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Duration;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;

public class RateLimitInterceptor implements HandlerInterceptor {

    private final RateLimiterService rateLimiterService;
    private final int LIMIT_REQUEST_PER_MINUTE = 10;
    private final int WAIT_MINUTES = 5;

    public RateLimitInterceptor(RateLimiterService rateLimiterService) {
        this.rateLimiterService = rateLimiterService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String clientIp = request.getRemoteAddr(); // Assuming the client id is passed in header
        String api = request.getRequestURI().startsWith("/api") ? "api" : request.getRequestURI();

        if (!rateLimiterService.isAllowed(clientIp, api, LIMIT_REQUEST_PER_MINUTE, Duration.ofMinutes(WAIT_MINUTES))) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            return false;
        }

        return true;
    }
}
