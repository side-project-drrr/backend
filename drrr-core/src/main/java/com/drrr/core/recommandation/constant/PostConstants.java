package com.drrr.core.recommandation.constant;

import lombok.Getter;

@Getter
public enum PostConstants {
    RECOMMEND_POSTS_COUNT(5);
    private final int value;

    PostConstants(final int value) {
        this.value = value;
    }
}
