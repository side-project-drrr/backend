package com.drrr.reader.fluent.blog;

import com.drrr.fluent.cralwer.core.WebDriverPool;
import com.drrr.fluent.cralwer.core.WebDriverPool.WebDriverPoolFactory;
import com.drrr.reader.AbstractCrawlerPageItemReader.CrawlingLocalDatePatterns;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.firefox.FirefoxOptions;

class SmailGateAiCrawlerTest {


    @Disabled
    @Test
    void 날짜_포맷_테스트() {
        var date = "2024-03-22T08:56:17+09:00";

        System.out.println(CrawlingLocalDatePatterns.PATTERN10.parse(date));
    }

    @Disabled
    @Test
    void 크롤링_테스트() {
        try (var wp = new WebDriverPool(new WebDriverPoolFactory(new FirefoxOptions()))) {
            var reader = new SmailGateAiCrawler()
                    .smailGateAiReader(wp);

            while (reader.read() != null) {
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}