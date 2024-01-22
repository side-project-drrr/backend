package com.drrr.domain.like.exception;

import com.drrr.core.exception.BaseCustomException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
public class TechBlogPostLikeException extends BaseCustomException {
    private final int code;

    private final List<Object> args;

    public TechBlogPostLikeException(int code, String message, Throwable ex) {
        super(code, message, ex);
        this.code = code;
        this.args = new ArrayList<>();
    }

    public TechBlogPostLikeException(int code, String message, Object... args) {
        super(code, String.format(message, args), args);
        this.code = code;
        this.args = Arrays.asList(args);
    }

    public TechBlogPostLikeException(int code, String message) {
        super(code, message);
        this.code = code;
        this.args = new ArrayList<>();
    }
}
