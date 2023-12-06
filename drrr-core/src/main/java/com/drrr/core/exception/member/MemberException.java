package com.drrr.core.exception.member;

import com.drrr.core.exception.BaseCustomException;
import lombok.EqualsAndHashCode;

// equals()와 hashCode() 메서드를 자동으로 생성
// callSuper = false : 부모 클래스인 RuntimeException의 필드는 고려하지 않겠다는 것
@EqualsAndHashCode(callSuper = false)
public class MemberException extends BaseCustomException {
    private final int code;
    public MemberException(final int code, final String message) {
        super(code, message);
        this.code = code;
    }
}
