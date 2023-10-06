package com.drrr.core.recommandation.constant.constant;

import lombok.Getter;

@Getter
public enum WeightConstants {
    // 최소 가중치 값
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

    WeightConstants(double value) {
        this.value = value;
    }


    public boolean isLessEqualThan(double value){
        return value <= this.value;
    }


    public double sum(double value) {
        return this.value + value;
    }
}