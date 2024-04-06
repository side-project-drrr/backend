package com.drrr.reader.fluent.blog;

import com.drrr.fluent.cralwer.core.WebDriverPool;
import com.drrr.fluent.cralwer.core.WebDriverPool.WebDriverPoolFactory;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.firefox.FirefoxOptions;

class BespinGlobalCrawlerTest {


    @Test
    @Disabled
    void 크롤링_테스트() {
        try (var webDriverPool = new WebDriverPool(
                new WebDriverPoolFactory(new FirefoxOptions().addArguments("--headless")))) {
            var reader = new BespinGlobalCrawler().bespinGlobalTechBlogReader(webDriverPool);

            while (reader.read() != null) {
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}