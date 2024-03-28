package com.drrr.alarm.service.request;

import java.time.LocalDate;
import java.util.List;
import lombok.Builder;

@Builder
public record PushDatesRequest(
        List<LocalDate> pushDates
) {
}
