package com.drrr.domain.techblogpost.constant;

import lombok.Getter;

@Getter
public enum RedisTtlConstants {
    FIVE_MINUTES(300),
    TEN_MINUTES(600);

    private final int ttl;

    RedisTtlConstants(int ttl) {
        this.ttl = ttl;
    }
}
