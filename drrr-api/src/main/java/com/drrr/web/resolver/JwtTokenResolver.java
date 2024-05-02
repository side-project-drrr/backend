package com.drrr.web.resolver;

import com.drrr.web.annotation.MemberId;
import com.drrr.web.annotation.Optional;
import com.drrr.web.exception.ApiExceptionCode;
import com.drrr.web.jwt.util.JwtProvider;
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

@Component
@RequiredArgsConstructor
public class JwtTokenResolver implements HandlerMethodArgumentResolver {
    private final JwtProvider jwtProvider;
    private static final String TOKEN_PREFIX = "Bearer ";

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(MemberId.class) && Long.class.equals(
                parameter.getParameterType());
    }

    //Jwt 토큰을 추출해서 파라미터로 넘겨준다.
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        final HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();

        final String tokenWithBearer = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (parameter.hasParameterAnnotation(Optional.class)) {
            return extractMemberIdFromToken(tokenWithBearer);
        }

        if (Objects.isNull(tokenWithBearer) || !tokenWithBearer.startsWith(TOKEN_PREFIX)) {
            throw new IllegalArgumentException(
                    ApiExceptionCode.JWT_NO_AUTHORIZATION_HEADER.newInstance());
        }

        return extractMemberIdFromToken(tokenWithBearer);
    }

    private Long extractMemberIdFromToken(final String tokenWithBearer) {
        if (tokenWithBearer == null) {
            return -1L;
        }

        final String token = tokenWithBearer.substring(TOKEN_PREFIX.length());

        if (!jwtProvider.validateToken(token)) {
            throw new IllegalArgumentException(ApiExceptionCode.JWT_UNAUTHORIZED.newInstance("토큰 검증 실패"));
        }

        return jwtProvider.extractToValueFrom(token);
    }
}
