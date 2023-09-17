package com.drrr.core.exception.admin;


import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum AdminExceptionCode {
    ADMIN(100, "정의되지 않은 에러입니다."),
    FAIL_SIGNIN(ADMIN.code + 1, "잘못된 관리자 Id 혹은 비밀번호 입니다.");

    private static final String ERROR_FORMAT = "[ERROR %d] %s";
    private final int code;
    private final String message;

    public AdminException invoke() {
        return new AdminException(code, String.format(ERROR_FORMAT, code, message));
    }
}
