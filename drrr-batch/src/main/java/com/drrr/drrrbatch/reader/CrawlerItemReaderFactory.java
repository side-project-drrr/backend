package com.drrr.drrrbatch.reader;

import com.drrr.drrrbatch.reader.impl.KakaoCrawlerItemReader;
import com.drrr.drrrbatch.reader.impl.MarketKurlyItemReader;
import com.drrr.drrrbatch.reader.impl.NaverCrawlerItemReader;
import com.drrr.drrrbatch.reader.impl.TestCrawlerPageItemReader;
import com.drrr.drrrbatch.reader.impl.WoowahanCrawlerItemReader;
import com.drrr.drrrjpa.domain.code.TechBlogCode;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.WebDriver;

@RequiredArgsConstructor
public class CrawlerItemReaderFactory {

    private final WebDriver webDriver;

    public AbstractCrawlerPageItemReader createItemReader(TechBlogCode code) {
        return this.findItemReaderBy(code).apply(webDriver);
    }

    public Function<WebDriver, AbstractCrawlerPageItemReader> findItemReaderBy(TechBlogCode code) {
        return switch (code) {
            case BASE -> TestCrawlerPageItemReader::new;
            case WOOWAHAN -> WoowahanCrawlerItemReader::new;
            case MARKET_KURLY -> MarketKurlyItemReader::new;
            case NAVER -> NaverCrawlerItemReader::new;
            case KAKAO -> KakaoCrawlerItemReader::new;
        };
    }
}
