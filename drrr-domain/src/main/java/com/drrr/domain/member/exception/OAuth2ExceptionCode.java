package com.drrr.domain.member.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum OAuth2ExceptionCode {
    OAUTH(2500, "정의되지 않은 에러입니다."),
    INVALID_ACCESS_TOKEN(OAUTH.code + 1,"OAuth2 Access Token이 유효하지 않습니다."),
    INVALID_AUTHORIZE_CODE(OAUTH.code+2, "OAuth2 Authorize Token이 유효하지 않습니다."),
    INVALID_OAUTH2_SUBJECT(OAUTH.code+3, "OAuth2 주체를 찾을 수 없습니다."),
    PROVIDER_ID_NULL(OAUTH.code+4, "Provider Id를 찾을 수 없습니다.")
    ;

    private final int code;
    private final String message;
    public OAuth2Exception newInstance() {
        return new OAuth2Exception(code, message);
    }

    public OAuth2Exception newInstance(Throwable ex) {
        return new OAuth2Exception(code, message, ex);
    }

    public OAuth2Exception newInstance(Object... args) {
        return new OAuth2Exception(code, String.format(message, args), args);
    }

    public OAuth2Exception newInstance(Throwable ex, Object... args) {
        return new OAuth2Exception(code, String.format(message, args), ex, args);
    }
}
