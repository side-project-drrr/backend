package com.drrr.alarm.dto;

import java.time.LocalDate;
import lombok.Builder;

@Builder
public record PushStatusRequest(
        Long memberId,
        LocalDate pushDate
) {
}
