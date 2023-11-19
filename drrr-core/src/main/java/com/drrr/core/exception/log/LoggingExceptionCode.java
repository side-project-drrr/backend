package com.drrr.core.exception.log;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Getter
public enum LoggingExceptionCode {
    //2000 코드는 메세지 직접 입력
    LOGGING(4000, "정의되지 않은 에러입니다."),
    INVALID_RECOMMEND_POSTS_LOGGING(LOGGING.code + 1,"기술 블로그 추천 후 로깅이 제대로 동작하지 않습니다."),
    ;

    private static final String ERROR_FORMAT = "[ERROR %d] %s";
    private final int code;
    private final String message;

    public LoggingException invoke() {
        return new LoggingException(code, message);
    }
}
