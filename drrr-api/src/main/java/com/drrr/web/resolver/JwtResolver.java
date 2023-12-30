package com.drrr.web.resolver;

import com.drrr.core.exception.jwt.JwtExceptionCode;
import com.drrr.web.jwt.util.JwtProvider;
import com.drrr.web.resolver.annotation.UserId;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@RequiredArgsConstructor
@Component
public class JwtResolver implements HandlerMethodArgumentResolver {
    private static final String TOKEN_PREFIX = "Bearer ";
    private final JwtProvider jwtTokenProvider;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(UserId.class) && Long.class.equals(
                parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        final HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();

        final String tokenWithBearer = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (Objects.isNull(tokenWithBearer) || !tokenWithBearer.startsWith(TOKEN_PREFIX)) {
            throw new IllegalArgumentException(
                    JwtExceptionCode.JWT_NO_AUTHORIZATION_HEADER.newInstance());
        }

        final String token = tokenWithBearer.substring(TOKEN_PREFIX.length());

        if (!jwtTokenProvider.validateToken(token)) {
            throw new IllegalArgumentException(JwtExceptionCode.JWT_UNAUTHORIZED.newInstance("토큰 검증 실패"));
        }

        return jwtTokenProvider.extractToValueFrom(token);
    }

}
