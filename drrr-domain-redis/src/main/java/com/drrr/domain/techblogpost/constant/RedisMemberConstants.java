package com.drrr.domain.techblogpost.constant;

import lombok.Getter;

@Getter
public enum RedisMemberConstants {
    GUEST(-1L);

    private final Long id;

    RedisMemberConstants(final Long id) {
        this.id = id;
    }
}
