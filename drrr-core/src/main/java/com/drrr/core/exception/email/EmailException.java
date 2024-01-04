package com.drrr.core.exception.email;

import com.drrr.core.exception.BaseCustomException;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@EqualsAndHashCode(callSuper = false)
public class EmailException extends BaseCustomException {
    private final int code;

    private final List<Object> args;

    public EmailException(int code, String message, Throwable ex) {
        super(code, message, ex);
        this.code = code;
        this.args = new ArrayList<>();
    }

    public EmailException(int code, String message, Object... args) {
        super(code, String.format(message, args), args);
        this.code = code;
        this.args = Arrays.asList(args);
    }

    public EmailException(int code, String message) {
        super(code, message);
        this.code = code;
        this.args = new ArrayList<>();
    }


}
