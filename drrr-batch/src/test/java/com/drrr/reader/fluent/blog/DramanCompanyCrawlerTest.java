package com.drrr.reader.fluent.blog;

import com.drrr.property.DriverProperty;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

class DramanCompanyCrawlerTest {


    @Test
    @Disabled
    void 크롤링테스트() throws Exception {
        //new DramanCompanyCrawler().DramanCompanyPageItemReader().read();
        var property = new DriverProperty("http://localhost:4444", "remote", new String[]{});

        var d1 = new RemoteWebDriver(property.getUrl(), new ChromeOptions());
        var d2 = new RemoteWebDriver(property.getUrl(), new EdgeOptions());

        d1.quit();
        d2.quit();


    }

}