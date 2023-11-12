package com.drrr.core.exception.login;

import com.drrr.core.exception.BaseCustomException;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
public class OAuth2Exception extends BaseCustomException {
    private final int code;
    public OAuth2Exception(final int code, final String message) {
        super(code, message);
        this.code = code;
    }
}
