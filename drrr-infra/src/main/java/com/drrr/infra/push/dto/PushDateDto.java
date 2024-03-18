package com.drrr.infra.push.dto;

import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record PushDateDto (
        //알림 아이콘에 느낌표 표시 여부
        boolean openStatus,
        boolean readStatus,
        long postCount,
        LocalDate pushDate
) {
    @QueryProjection
    public PushDateDto(boolean openStatus, boolean readStatus, long postCount, LocalDate pushDate) {
        this.openStatus = openStatus;
        this.readStatus = readStatus;
        this.postCount = postCount;
        this.pushDate = pushDate;
    }
}

