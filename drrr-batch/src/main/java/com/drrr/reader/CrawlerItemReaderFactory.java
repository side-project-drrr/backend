package com.drrr.reader;

import com.drrr.core.code.techblog.TechBlogCode;
import com.drrr.domain.ExternalBlogPosts;
import com.drrr.reader.impl.KakaoCrawlerItemReader;
import com.drrr.reader.impl.MarketKurlyItemReader;
import com.drrr.reader.impl.NaverCrawlerItemReader;
import com.drrr.reader.impl.TestCrawlerPageItemReader;
import com.drrr.reader.impl.WoowahanCrawlerItemReader;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.WebDriver;
import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CrawlerItemReaderFactory {

    private final WebDriver webDriver;

    public ItemReader<ExternalBlogPosts> createItemReader(TechBlogCode code) {
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
