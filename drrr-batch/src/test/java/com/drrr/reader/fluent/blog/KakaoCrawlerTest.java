package com.drrr.reader.fluent.blog;

import com.drrr.fluent.cralwer.core.WebDriverPool;
import com.drrr.fluent.cralwer.core.WebDriverPool.WebDriverPoolFactory;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.firefox.FirefoxOptions;

class KakaoCrawlerTest {


    @Test
    @Disabled
    void 크롤링_테스트() throws Exception {
        var webDriverPoolFactory = new WebDriverPoolFactory(new FirefoxOptions().addArguments("--headless"));
        try (var pool = new WebDriverPool(webDriverPoolFactory)) {

            var reader = new KakaoCrawler().kakaoReader(pool);

            while (reader.read() != null) {
            }
        }
        //44

        //1 -> 73
        //5 -> 33
        //3 -> 56
        //5 -> 59
        //5 -> 40
    }

}