package com.drrr.core.code.redis;

import lombok.Getter;

@Getter
public enum RedisTTL {
    EXPIRE_CACHE(300),
    EXPIRE_ACCESS_TOKEN(604800),
    EXPIRE_REFRESH_TOKEN(1209600);

    private final long seconds;

    RedisTTL(long seconds) {
        this.seconds = seconds;
    }
}
