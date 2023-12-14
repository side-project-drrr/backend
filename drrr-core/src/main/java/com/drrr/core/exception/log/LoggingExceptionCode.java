package com.drrr.core.exception.log;

import com.drrr.core.exception.jwt.JwtException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Getter
public enum LoggingExceptionCode {
    //2000 코드는 메세지 직접 입력
    LOGGING(4000, "정의되지 않은 에러입니다."),
    INVALID_RECOMMEND_POSTS_LOGGING(LOGGING.code + 1,"기술 블로그 추천 후 로깅이 제대로 동작하지 않습니다."),
    ;

    private final int code;
    private final String message;
    public LoggingException newInstance() {
        return new LoggingException(code, message);
    }

    public LoggingException newInstance(Throwable ex) {
        return new LoggingException(code, message, ex);
    }

    public LoggingException newInstance(Object... args) {
        return new LoggingException(code, String.format(message, args), args);
    }

    public LoggingException newInstance(Throwable ex, Object... args) {
        return new LoggingException(code, String.format(message, args), ex, args);
    }
}
