package com.drrr.core.exception.category;

import com.drrr.core.exception.email.EmailException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum CategoryExceptionCode {
    CATEGORY(3500, "정의되지 않은 에러입니다."),
    CATEGORY_NOT_FOUND(CATEGORY.code + 1,"카테고리를 찾을 수 없습니다."),
    CATEGORY_WEIGHT_NOT_FOUND(CATEGORY.code+2,"카테고리 가중치 정보를 찾을 수 없습니다."),
    MEMBER_CATEGORY_UNCHANGD(CATEGORY.code+3,"수정된 카테고리가 없습니다."),
    ;
    private final int code;
    private final String message;
    public CategoryException newInstance() {
        return new CategoryException(code, message);
    }

    public CategoryException newInstance(Throwable ex) {
        return new CategoryException(code, message, ex);
    }

    public CategoryException newInstance(Object... args) {
        return new CategoryException(code, String.format(message, args), args);
    }

    public CategoryException newInstance(Throwable ex, Object... args) {
        return new CategoryException(code, String.format(message, args), ex, args);
    }
}
