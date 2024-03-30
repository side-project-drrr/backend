package com.drrr.fluent.cralwer.core;

/**
 * log 혹은 추출된 데이터에 특정 상태 변화를 주고 싶은 경우 사용
 *
 * @param <T>
 */
public interface After<T> {
    void action(T input);
}
