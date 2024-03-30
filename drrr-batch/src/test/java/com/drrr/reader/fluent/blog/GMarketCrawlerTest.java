package com.drrr.reader.fluent.blog;

import com.drrr.fluent.cralwer.core.WebDriverPool;
import com.drrr.fluent.cralwer.core.WebDriverPool.WebDriverPoolFactory;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.firefox.FirefoxOptions;

class GMarketCrawlerTest {


    @Disabled
    @Test
    void 크롤링_테스트() {
        try (var pool = new WebDriverPool(new WebDriverPoolFactory(new FirefoxOptions()))) {
            var gMarketCrawler = new GMarketCrawler().gMargetReader(pool);

            while (gMarketCrawler.read() != null) {
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}