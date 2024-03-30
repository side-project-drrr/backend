package com.drrr.reader.fluent.blog;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.firefox.FirefoxDriver;

class DramanCompanyCrawlerTest {


    @Test
    @Disabled
    void 크롤링테스트() throws Exception {
        new DramanCompanyCrawler().DramanCompanyPageItemReader(new FirefoxDriver()).read();
    }

}