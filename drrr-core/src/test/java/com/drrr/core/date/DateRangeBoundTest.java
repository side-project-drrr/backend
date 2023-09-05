package com.drrr.core.date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.time.LocalDate;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;


@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class DateRangeBoundTest {


    // happy test
    @Test
    void 시작일과_종료일을_가질_수_있습니다() {

        final LocalDate startDate = LocalDate.now();
        final LocalDate lastDate = LocalDate.now().plusDays(1);
        final DateRangeBound rangeBound = DateRangeBound.builder()
                .startDate(startDate)
                .lastDate(lastDate)
                .build();

        assertThat(rangeBound.getStartDate()).isEqualTo(startDate);
        assertThat(rangeBound.getLastDate()).isEqualTo(lastDate);
    }

    @Test
    void 시작일과_종료일_둘_중에_하나만_입력될_경우_동일한_날짜를_가집니다() {

        final DateRangeBound dateRangeBound = DateRangeBound.createSingleRange(LocalDate.now());

        assertThat(dateRangeBound.getLastDate()).isEqualTo(LocalDate.now());
        assertThat(dateRangeBound.getLastDate()).isEqualTo(dateRangeBound.getStartDate());
    }

    @Test
    void 둘_중_하나라도_존재하지_않는다면_에러가_발생합니다() {

        assertAll(() -> {
            // 시작일만 존재하는 경우
            assertThatNullPointerException().isThrownBy(() -> {
                DateRangeBound.builder()
                        .startDate(LocalDate.now())
                        .build();
            });

            assertThatNullPointerException().isThrownBy(() -> {
                DateRangeBound.builder()
                        .lastDate(LocalDate.now())
                        .build();
            });
        });
    }

    @Test
    void 시작일이_종료일보다_늦는_경우_에러가_발생한다() {
        assertThatIllegalArgumentException().isThrownBy(() ->
                DateRangeBound.builder()
                        .startDate(LocalDate.now().plusDays(1))
                        .lastDate(LocalDate.now())
                        .build());
    }

}