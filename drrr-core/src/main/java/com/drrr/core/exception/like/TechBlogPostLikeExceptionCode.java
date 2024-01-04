package com.drrr.core.exception.like;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TechBlogPostLikeExceptionCode {
    LIKE(4000, "정의되지 않은 에러입니다."),
    DUPLICATE_LIKE(LIKE.code + 1, "사용자가 하나의 게시물에 중복으로 좋아요를 눌렀습니다."),
    ;
    private final int code;
    private final String message;

    public TechBlogPostLikeException newInstance() {
        return new TechBlogPostLikeException(code, message);
    }

    public TechBlogPostLikeException newInstance(Throwable ex) {
        return new TechBlogPostLikeException(code, message, ex);
    }

    public TechBlogPostLikeException newInstance(Object... args) {
        return new TechBlogPostLikeException(code, String.format(message, args), args);
    }

    public TechBlogPostLikeException newInstance(Throwable ex, Object... args) {
        return new TechBlogPostLikeException(code, String.format(message, args), ex, args);
    }
}
