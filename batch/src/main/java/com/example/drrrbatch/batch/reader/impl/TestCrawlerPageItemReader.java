package com.example.drrrbatch.batch.reader.impl;

import static com.example.drrrbatch.batch.reader.CrawlerPageStrategy.SINGLE_PAGE;

import com.example.drrrbatch.batch.domain.ExternalBlogPosts;
import com.example.drrrbatch.batch.reader.AbstractCrawlerPageItemReader;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;

@Slf4j
public class TestCrawlerPageItemReader extends AbstractCrawlerPageItemReader {
    public TestCrawlerPageItemReader(WebDriver webDriver) {
        super(SINGLE_PAGE, webDriver);
    }

    @Override
    protected ExternalBlogPosts executeCrawlerPage() {
        log.info("아직 구현하지 않은 ItemReader 호출");
        return null;
    }


}
