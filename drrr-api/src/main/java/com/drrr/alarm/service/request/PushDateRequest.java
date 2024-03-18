package com.drrr.alarm.service.request;

import java.time.LocalDate;
import lombok.Builder;

@Builder
public record
PushDateRequest(
        LocalDate from,
        LocalDate to
) {
}
