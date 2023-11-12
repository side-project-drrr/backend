package com.drrr.core.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public abstract class BaseCustomException extends RuntimeException{
    private final int code;

    public BaseCustomException(int code, String message){
        super(message);
        this.code = code;
    }
}
