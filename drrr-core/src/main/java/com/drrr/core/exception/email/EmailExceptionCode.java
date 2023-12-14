package com.drrr.core.exception.email;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum EmailExceptionCode {
    EMAIL(6000, "정의되지 않은 에러입니다."),
    EMAIL_VERIFICATION_INFORMATION_NOT_FOUND(EMAIL.code + 1, "이메일 인증 정보를 찾을 수 없습니다."),
    EMAIL_VERIFICATION_CODE_EXPIRED(EMAIL.code + 2, "이메일 인증코드가 유효하지 않습니다."),
    ;
    private final int code;
    private final String message;
    public EmailException newInstance() {
        return new EmailException(code, message);
    }

    public EmailException newInstance(Throwable ex) {
        return new EmailException(code, message, ex);
    }

    public EmailException newInstance(Object... args) {
        return new EmailException(code, String.format(message, args), args);
    }

    public EmailException newInstance(Throwable ex, Object... args) {
        return new EmailException(code, String.format(message, args), ex, args);
    }
}
