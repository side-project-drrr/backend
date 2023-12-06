package com.drrr.core.exception.admin;


import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
public class AdminException extends RuntimeException {

    private final int code;

    public AdminException(final int code, final String message) {
        super(message);
        this.code = code;
    }
}
