package com.drrr.core.exception.jwt;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum JwtExceptionCode {
    JWT(3000, "정의되지 않은 에러입니다."),
    EXTRACT_VALUE_FROM_TOKEN_FAILED(JWT.code + 1,"JWT 토큰으로부터 값을 추출할 수 없습니다."),
    INVALID_JWT_SIGNATURE(JWT.code + 2,"JWT 토큰 시그니처가 유효하지 않습니다."),
    JWT_TOKEN_EXPIRED(JWT.code + 3,"JWT 토큰 시그니처가 유효하지 않습니다."),
    ;

    private static final String ERROR_FORMAT = "[ERROR %d] %s";
    private final int code;
    private final String message;
}
