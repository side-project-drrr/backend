package com.drrr.core.recommandation.constant.constant;

import lombok.Getter;

@Getter
public enum PostConstants {
    RECOMMEND_POSTS_COUNT(5);
    private final int value;
    PostConstants(int value){
        this.value = value;
    }
}
