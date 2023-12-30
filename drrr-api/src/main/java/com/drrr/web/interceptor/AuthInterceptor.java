package com.drrr.web.interceptor;

import com.drrr.web.interceptor.annotation.Auth;
import com.drrr.web.jwt.util.JwtProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@RequiredArgsConstructor
@Component
public class AuthInterceptor implements HandlerInterceptor {

    private final JwtProvider jwtProvider;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) {

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Auth auth = handlerMethod.getMethodAnnotation(Auth.class);

        //Controller api 중 @Auth 어노테이션이 붙여있는 경우에 jwt 토큰 헤더가 있는 경우만 통과
        if (!Objects.isNull(auth)) {
            return jwtProvider.jwtHeaderExists(request);
        }

        return true;
    }

}
