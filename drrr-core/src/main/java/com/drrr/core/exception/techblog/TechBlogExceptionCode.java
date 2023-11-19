package com.drrr.core.exception.techblog;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Getter
public enum TechBlogExceptionCode {
    //2000 코드는 메세지 직접 입력
    TECH_BLOG(3000, "정의되지 않은 에러입니다."),
    TECH_BLOG_NOT_FOUND(TECH_BLOG.code + 1,"기술블로그를 찾을 수 없습니다."),
    ;

    private static final String ERROR_FORMAT = "[ERROR %d] %s";
    private final int code;
    private final String message;

    public TechBlogException invoke() {
        return new TechBlogException(code, message);
    }
}
