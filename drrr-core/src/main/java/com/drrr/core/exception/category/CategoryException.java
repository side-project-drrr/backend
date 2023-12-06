package com.drrr.core.exception.category;

import com.drrr.core.exception.BaseCustomException;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
public class CategoryException extends BaseCustomException {
    private final int code;
    public CategoryException(final int code, final String message) {
        super(code, message);
        this.code = code;
    }
}
