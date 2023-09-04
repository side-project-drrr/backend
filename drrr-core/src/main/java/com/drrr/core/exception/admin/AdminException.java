package com.drrr.core.exception.admin;


import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
public class AdminException extends RuntimeException {

    private final int code;

    public AdminException(int code, String message) {
        super(message);
        this.code = code;
    }
}
