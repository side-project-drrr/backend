package com.drrr.core.exception.email;

import com.drrr.core.exception.BaseCustomException;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
public class EmailException extends BaseCustomException {
    private final int code;
    public EmailException(final int code, final String message) {
        super(code, message);
        this.code = code;
    }

}
