package com.drrr.core.exception.jwt;

import com.drrr.core.exception.BaseCustomException;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
public class JwtException extends BaseCustomException {
    private final int code;
    public JwtException(final int code, final String message) {
        super(code, message);
        this.code = code;
    }
}
