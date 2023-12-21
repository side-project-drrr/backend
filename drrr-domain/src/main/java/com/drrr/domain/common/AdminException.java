package com.drrr.domain.common;


import lombok.Getter;

@Getter
public class AdminException extends RuntimeException {

    private final int code;


    public AdminException(int code, String message) {
        super(message);
        this.code = code;
    }
}
