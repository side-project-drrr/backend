package com.drrr.core.exception.member;

import com.drrr.core.exception.log.LoggingException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Getter
public enum MemberExceptionCode {
    //2000 코드는 메세지 직접 입력
    MEMBER(2000, "정의되지 않은 에러입니다."),
    INVALID_ACCESS_TOKEN(MEMBER.code + 1,"OAuth2 Access Token이 유효하지 않습니다."),
    INVALID_AUTHORIZE_CODE(MEMBER.code+2, "OAuth2 Authorize Token이 유효하지 않습니다."),
    INVALID_OAUTH2_SUBJECT(MEMBER.code+3, "OAuth2 주체를 찾을 수 없습니다."),
    DUPLICATE_EMAIL(MEMBER.code+4, "이메일이 이미 등록되어 있습니다."),
    DUPLICATE_NICKNAME(MEMBER.code+5, "닉네임이 이미 등록되어 있습니다."),
    UNKNOWN_ERROR(MEMBER.code+6, "알 수 없는 오류가 발생했습니다."),
    UNREGISTERED_MEMBER(MEMBER.code+7, "등록되지 않은 사용자입니다."),
    INVALID_MEMBER_WEIGHT(MEMBER.code+8, "사용자 가중치를 찾을 수 없습니다."),
    MEMBER_NOT_FOUND(MEMBER.code+9, "사용자를 찾을 수 없습니다."),
    ;

    private final int code;
    private final String message;
    public MemberException newInstance() {
        return new MemberException(code, message);
    }

    public MemberException newInstance(Throwable ex) {
        return new MemberException(code, message, ex);
    }

    public MemberException newInstance(Object... args) {
        return new MemberException(code, String.format(message, args), args);
    }

    public MemberException newInstance(Throwable ex, Object... args) {
        return new MemberException(code, String.format(message, args), ex, args);
    }
}
