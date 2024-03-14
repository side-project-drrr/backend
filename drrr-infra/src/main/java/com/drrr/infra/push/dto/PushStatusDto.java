package com.drrr.infra.push.dto;

import com.querydsl.core.annotations.QueryProjection;

public record PushStatusDto(
        boolean readStatus,
        boolean openStatus
) {
    @QueryProjection
    public PushStatusDto(boolean readStatus, boolean openStatus) {
        this.readStatus = readStatus;
        this.openStatus = openStatus;
    }
}
