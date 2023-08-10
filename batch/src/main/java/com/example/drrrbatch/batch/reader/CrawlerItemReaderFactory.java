package com.example.drrrbatch.batch.reader;

import com.example.drrrbatch.batch.reader.impl.TestCrawlerPageItemReader;
import com.example.drrrbatch.batch.vo.TechBlogCode;
import java.util.function.Function;
import java.util.function.Supplier;
import org.openqa.selenium.WebDriver;
import org.springframework.stereotype.Component;

@Component
public class CrawlerItemReaderFactory {

    public AbstractCrawlerPageItemReader createItemReader(TechBlogCode code, Supplier<WebDriver> supplier) {
        return this.createItemReader(code).apply(supplier.get());
    }

    public Function<WebDriver, AbstractCrawlerPageItemReader> createItemReader(TechBlogCode code) {
        return switch (code) {
            case BASE -> TestCrawlerPageItemReader::new;
            case MARKET_KURLY, NAVER -> null;
        };
    }
}
