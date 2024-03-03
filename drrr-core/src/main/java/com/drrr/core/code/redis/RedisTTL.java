package com.drrr.core.code.redis;

import lombok.Getter;

@Getter
public enum RedisTTL {
    ONE_HOUR(3600),
    ONE_DAY(86400);

    private final long seconds;

    RedisTTL(long seconds) {
        this.seconds = seconds;
    }
}
