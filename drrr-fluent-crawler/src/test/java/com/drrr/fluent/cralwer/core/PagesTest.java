package com.drrr.fluent.cralwer.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.drrr.fluent.cralwer.core.Pages.PagesBuilder;
import com.drrr.fluent.cralwer.core.PaginationReader.PaginationInformation;
import com.drrr.fluent.cralwer.util.FakeWebDriver;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;


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

    @Test
    void _5번의_페이지_호출중에서_크롤링_중단_상황이_발생하면_멈춥니다() {
        var pages = new PagesBuilder<String>()
                .contentsReader(webDriver -> "")
                .contentsLoader(webDriverWait -> {
                })
                .pagesInitializer(pageNumber -> "")
                .paginationReader(webDriver -> new PaginationInformation(5))
                .webDriver(new FakeWebDriver())
                .pagesStopper(new PagesStopper() {
                    final Deque<Integer> step = new ArrayDeque<>(List.of(1, 2, 3));

                    @Override
                    public boolean isSatisfy(WebDriver webDriver, WebDriverWait webDriverWait) {
                        if (step.isEmpty()) {
                            return true;
                        }
                        step.poll();
                        return false;
                    }
                })
                .build();

        assertAll(
                () -> assertThat(pages.execute()).isEqualTo(""),
                () -> assertThat(pages.execute()).isEqualTo(""),
                () -> assertThat(pages.execute()).isEqualTo(""),
                () -> assertThat(pages.execute()).isEqualTo(null),
                () -> assertThat(pages.execute()).isEqualTo(null)
        );

    }

}