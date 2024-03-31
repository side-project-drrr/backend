package com.drrr.reader.fluent.blog;

import static org.assertj.core.api.Assertions.assertThat;

import com.drrr.reader.AbstractCrawlerPageItemReader.CrawlingLocalDatePatterns;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class SquareLabCrawlerTest {


    @Test
    void local() {
        var str = "2023년 8월 30일";

        var date = CrawlingLocalDatePatterns.PATTERN8.parse(str);

        assertThat(date.getYear()).isEqualTo(2023);
        assertThat(date.getMonthValue()).isEqualTo(8);
    }

    @Test
    @Disabled
    void 크롤링_테스트() throws Exception {
        //new SquareLabCrawler().squareLabPageReader().read();
    }
}