package com.drrr.core.exception;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public abstract class BaseCustomException extends RuntimeException{
    private final int code;

    private final List<Object> args;

    public BaseCustomException(final int code,final String message, final Throwable ex) {
        super(message, ex);
        this.code = code;
        this.args = new ArrayList<>();
    }

    public BaseCustomException(final int code, final String message) {
        super(message);
        this.code = code;
        this.args = new ArrayList<>();
    }
    public BaseCustomException(final int code, final String message, final Object... args) {
        super(message);
        this.code = code;
        this.args = Arrays.asList(args);
    }

}
