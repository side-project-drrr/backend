package com.drrr.domain.member.exception;

import com.drrr.core.exception.BaseCustomException;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@EqualsAndHashCode(callSuper = false)
public class OAuth2Exception extends BaseCustomException {
    private final int code;

    private final List<Object> args;

    public OAuth2Exception(int code, String message, Throwable ex) {
        super(code, message, ex);
        this.code = code;
        this.args = new ArrayList<>();
    }

    public OAuth2Exception(int code, String message, Object... args) {
        super(code, String.format(message, args), args);
        this.code = code;
        this.args = Arrays.asList(args);
    }

    public OAuth2Exception(int code, String message) {
        super(code, message);
        this.code = code;
        this.args = new ArrayList<>();
    }
}
