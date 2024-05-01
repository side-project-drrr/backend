package com.drrr.domain.exception;

import java.util.function.Supplier;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum RedisDomainExceptionCode {
    REDIS(6000, "정의되지 않은 에러입니다."),
    REDIS_POST_DYNAMIC_DATA_NOT_FOUND(REDIS.code + 1, "Redis 게시물 동적 데이터를 찾을 수 없습니다.");

    private final int code;
    private final String message;

    public RedisDomainException newInstance() {
        return new RedisDomainException(code, message);
    }

    public RedisDomainException newInstance(Throwable ex) {
        return new RedisDomainException(code, message, ex);
    }

    public RedisDomainException newInstance(Object... args) {
        return new RedisDomainException(code, String.format(message, args), args);
    }

    public RedisDomainException newInstance(Throwable ex, Object... args) {
        return new RedisDomainException(code, String.format(message, args), ex, args);
    }

    public void invokeBySupplierCondition(Supplier<Boolean> condition) {
        if (condition.get()) {
            throw new RedisDomainException(code, message);
        }
    }

    public void invokeByCondition(boolean condition) {
        if (condition) {
            throw new RedisDomainException(code, message);
        }
    }
}
