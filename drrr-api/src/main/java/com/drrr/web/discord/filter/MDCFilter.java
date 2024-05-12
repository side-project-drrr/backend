package com.drrr.web.discord.filter;

import com.drrr.web.discord.constant.DiscordConstants;
import com.drrr.web.discord.util.HttpRequestUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class MDCFilter extends OncePerRequestFilter {
    @Value("${discord.webhook.key}")
    private String discordWebhookUrl;

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
                                    final FilterChain filterChain)
            throws ServletException, IOException {

        MDC.put(DiscordConstants.IP_ADDRESS, request.getRemoteAddr());
        MDC.put(DiscordConstants.REQUEST_URI, request.getRequestURI());
        MDC.put(DiscordConstants.METHOD, request.getMethod());
        MDC.put(DiscordConstants.HEADERS, HttpRequestUtil.getHeaderMap(request));
        MDC.put(DiscordConstants.PARAMS, HttpRequestUtil.getParamMap(request));
        MDC.put(DiscordConstants.BODY, HttpRequestUtil.getBody(request));
        MDC.put(DiscordConstants.WEBHOOK_URL, discordWebhookUrl);

        filterChain.doFilter(request, response);
    }
}
