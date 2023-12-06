package com.drrr.web.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
//필터 중에서 가장 먼저 동작하도록 함
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CORSFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
                                    final FilterChain filterChain)
            throws ServletException, IOException {
        //모든 도메인에서의 접근을 허용하기 위해 * 값을 설정합니다. 만약 특정 도메인만 허용하고 싶다면 해당 도메인을 명시합니다.
        response.setHeader("Access-Control-Allow-Origin", "*");
        // 요청에 자격 증명(쿠키, 인증 정보 등)을 포함시키기 위해 true로 설정합니다. 필요한 경우에만 사용하세요.
        response.setHeader("Access-Control-Allow-Credentials", "true");
        //모든 HTTP 메서드를 허용하기 위해 * 값을 설정합니다. 필요한 경우에는 실제로 허용할 메서드만 명시할 수도 있습니다.
        response.setHeader("Access-Control-Allow-Methods", "*");
        //Preflight 요청의 캐시 지속 시간을 설정합니다. Preflight 요청은 실제 요청 전에 브라우저가 보내는 추가적인 OPTIONS 메서드를 통한 사전 검사 요청입니다.
        response.setHeader("Access-Control-Max-Age", "3600");
        //허용되는 요청 헤더를 설정합니다. 위 코드에서는 일반적으로 사용되는 헤더들을 포함시켰으며, 필요에 따라 추가적인 헤더를 명시할 수 있습니다.
        response.setHeader("Access-Control-Allow-Headers",
                "Origin, X-Requested-With, Content-Type, Accept, Key, Authorization");

        /**
         * 마지막으로, OPTIONS 메서드인 경우 응답의 상태 코드를 200으로 설정하여 Preflight 요청에 대한 처리를 완료합니다.
         * 그 외의 경우에는 실제 요청을 처리하기 위해 FilterChain으로 제어를 넘깁니다.
         */
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            filterChain.doFilter(request, response);
        }
    }
}
