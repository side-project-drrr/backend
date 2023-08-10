package com.example.drrrbatch.batch.reader;

import com.example.drrrbatch.batch.reader.impl.MarketKurlyItemReader;
import com.example.drrrbatch.batch.reader.impl.NaverCrawlerItemReader;
import com.example.drrrbatch.batch.reader.impl.TestCrawlerPageItemReader;
import com.example.drrrbatch.batch.vo.TechBlogCode;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.WebDriver;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CrawlerItemReaderFactory {

    private final WebDriver webDriver;

    public AbstractCrawlerPageItemReader createItemReader(TechBlogCode code) {
        return this.findItemReaderBy(code).apply(webDriver);
    }

    public Function<WebDriver, AbstractCrawlerPageItemReader> findItemReaderBy(TechBlogCode code) {
        return switch (code) {
            case BASE -> TestCrawlerPageItemReader::new;
            case MARKET_KURLY -> MarketKurlyItemReader::new;
            case NAVER -> NaverCrawlerItemReader::new;
        };
    }
}
