package com.drrr.core.recommandation.constant.constant;

import lombok.Getter;

@Getter
public enum HoursConstants {
    // 가중치를 감소시켜주는 최소 unread hours 값
    PAST_HOURS(8);
    private int value;
    HoursConstants(int value){
        this.value = value;
    }
}
