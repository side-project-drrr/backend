package com.drrr.domain.common;


import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum AdminExceptionCode {
    TEMPORAL_TECH_BLOG(1000),

    REGISTER_COMPLETE_TAG(TEMPORAL_TECH_BLOG.code + 1, "이미 태그 등록이 완료된 임시 게시글 입니다."),
    DID_NOT_EXISTS_TAG_NAMES(TEMPORAL_TECH_BLOG.code + 2, "생성할 태그가 존재하지 않습니다."),
    DID_NOT_EXISTS_TEMPORAL_POST(TEMPORAL_TECH_BLOG.code + 3, "존재하지 않는 임시 게시글 입니다."),
    DID_NOT_EXISTS_REFERENCE_ID(TEMPORAL_TECH_BLOG.code + 4, "존재하지 않는 참조 카테고리 아이디입니다.");

    private final int code;
    private final String message;

    AdminExceptionCode(int code) {
        this.code = code;
        this.message = "";
    }

    public AdminException create() {
        return new AdminException(this.code, this.message);
    }

    public void invokeByCondition(boolean condition) {
        if (condition) {
            throw this.create();
        }
    }
}
