package com.drrr.drrrbatch.reader.impl;

import com.drrr.drrrbatch.domain.ExternalBlogPosts;
import com.drrr.drrrbatch.reader.AbstractCrawlerPageItemReader;
import com.drrr.drrrbatch.reader.CrawlerPageStrategy;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;

@Slf4j
public class TestCrawlerPageItemReader extends AbstractCrawlerPageItemReader {
    public TestCrawlerPageItemReader(WebDriver webDriver) {
        super(CrawlerPageStrategy.SINGLE_PAGE, webDriver);
    }

    @Override
    protected ExternalBlogPosts executeCrawlerPage() {
        log.info("아직 구현하지 않은 ItemReader 호출");
        return null;
    }

}
