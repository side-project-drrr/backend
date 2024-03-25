package com.drrr.fluent.cralwer.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.drrr.fluent.cralwer.core.PaginationReader.PaginationInformation;
import com.drrr.fluent.cralwer.util.FakeWebDriver;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;


@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class PagesTest {

    @Test
    void _5번의_페이지_호출_후_null을_반환합니다() {

        var pages = Pages.<String>builder()
                .contentsReader(webDriver -> "")
                .contentsLoader(webDriverWait -> {
                })
                .pagesInitializer(pageNumber -> "")
                .paginationReader(webDriver -> new PaginationInformation(5))
                .webDriver(new FakeWebDriver())
                .build();

        assertAll(
                () -> assertThat(pages.execute()).isEqualTo(""),
                () -> assertThat(pages.execute()).isEqualTo(""),
                () -> assertThat(pages.execute()).isEqualTo(""),
                () -> assertThat(pages.execute()).isEqualTo(""),
                () -> assertThat(pages.execute()).isEqualTo(""),

                () -> assertThat(pages.execute()).isEqualTo(null)
        );

    }

}