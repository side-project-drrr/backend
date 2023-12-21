package com.drrr.config;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Slf4j
@Configuration
@ConditionalOnBean(WebCrawlerBatchConfiguration.class) // Crawling 실행시 빈으로 등록하도록 변경
public class DriverConfiguration {
    @Bean
    public WebDriver webDriver() {
        return new FirefoxDriver(new FirefoxOptions()
                .addArguments("--headless"));
    }
}
