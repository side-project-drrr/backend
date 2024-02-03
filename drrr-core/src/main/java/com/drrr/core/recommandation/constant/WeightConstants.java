package com.drrr.core.recommandation.constant;

import lombok.Getter;

@Getter
public enum WeightConstants {
    // 최소 가중치 값(주의, MIN_CONDITIONAL_WEIGHT 값보다 높게 설정하면 안됨, validation에서 문제 발생)
    MIN_WEIGHT(0.0),
    // 최대 가중치 값
    MAX_WEIGHT(10.0),
    // 선호하는 카테고리에 대한 최소 가중치 값
    MIN_CONDITIONAL_WEIGHT(1.0),
    // 감소하는 최소 가중치 값
    DECREASE_WEIGHT(1.0),

    // 증가하는 최소 가중치 값
    INCREASE_WEIGHT(1.0);
    private final double value;

    WeightConstants(final double value) {
        this.value = value;
    }


    public boolean isGreaterThan(final double value) {
        return value < this.value;
    }


    public double sum(final double value) {
        return this.value + value;
    }
}