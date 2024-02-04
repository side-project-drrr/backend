package com.drrr.alarm.dto;

import java.time.LocalDate;
import lombok.Builder;

@Builder
public record PushRequest(
        Long memberId,
        LocalDate pushDate
) {
}
