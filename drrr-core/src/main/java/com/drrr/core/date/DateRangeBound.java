package com.drrr.core.date;

import java.time.LocalDate;
import java.util.Objects;
import lombok.Builder;
import lombok.Getter;


@Getter
public final class DateRangeBound {
    private final LocalDate startDate;
    private final LocalDate lastDate;

    @Builder
    private DateRangeBound(LocalDate startDate, LocalDate lastDate) {
        Objects.requireNonNull(startDate, "시작일이 존재하지 않습니다.");
        Objects.requireNonNull(lastDate, "종료알이 존재하지 않습니다.");

        if (startDate.isAfter(lastDate)) {
            throw new IllegalArgumentException("시작일이 종료일보다 늦습니다.");
        }
        this.startDate = startDate;
        this.lastDate = lastDate;
    }

    public static DateRangeBound createSingleRange(LocalDate date) {
        return new DateRangeBound(date, date);
    }
}
