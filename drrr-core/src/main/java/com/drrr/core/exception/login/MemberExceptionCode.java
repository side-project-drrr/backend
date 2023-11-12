package com.drrr.core.exception.login;

import com.drrr.core.exception.admin.AdminException;
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
    DUPLICATE_NICKNAME(MEMBER.code+5, "닉네임인 이미 등록되어 있습니다."),
    UNKNOWN_ERROR(MEMBER.code+6, "알 수 없는 오류가 발생했습니다."),
    UNREGISTERED_MEMBER(MEMBER.code+7, "등록되지 않은 사용자입니다."),
    ;

    private static final String ERROR_FORMAT = "[ERROR %d] %s";
    private final int code;
    private final String message;

    public MemberException invoke() {
        return new MemberException(code, message);
    }
}
