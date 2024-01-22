package com.drrr.domain.techblogpost.exception;

import com.drrr.core.exception.BaseCustomException;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// equals()와 hashCode() 메서드를 자동으로 생성
// callSuper = false : 부모 클래스인 RuntimeException의 필드는 고려하지 않겠다는 것
@EqualsAndHashCode(callSuper = false)
public class TechBlogException extends BaseCustomException {
    private final int code;

    private final List<Object> args;

    public TechBlogException(int code, String message, Throwable ex) {
        super(code, message, ex);
        this.code = code;
        this.args = new ArrayList<>();
    }

    public TechBlogException(int code, String message, Object... args) {
        super(code, String.format(message, args), args);
        this.code = code;
        this.args = Arrays.asList(args);
    }

    public TechBlogException(int code, String message) {
        super(code, message);
        this.code = code;
        this.args = new ArrayList<>();
    }
}
