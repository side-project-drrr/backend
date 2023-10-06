package com.drrr.core.recommandation.constant.constant;

import lombok.Getter;

@Getter
public enum DaysConstants {
    //특정 Days 동안 읽지 않으면 가중치를 0 또는 1로 초기화 해주기 위한 값
    UNREAD_DAYS(3);
    private final int value;
    DaysConstants(int value){
        this.value = value;
    }
}
