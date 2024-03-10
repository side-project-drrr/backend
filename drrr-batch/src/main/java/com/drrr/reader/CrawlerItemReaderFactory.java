package com.drrr.reader;

import com.drrr.core.code.techblog.TechBlogCode;
import com.drrr.domain.ExternalBlogPosts;
import com.drrr.reader.impl.BespinGlobalCrawlerItemReader;
import com.drrr.reader.impl.DevOceanCrawlerItemReader;
import com.drrr.reader.impl.DevSistersCrawlerItemReader;
import com.drrr.reader.impl.KakaoCrawlerItemReader;
import com.drrr.reader.impl.LineCrawlerItemReader;
import com.drrr.reader.impl.MarketKurlyItemReader;
import com.drrr.reader.impl.NHNCloudCrawlerItemReader;
import com.drrr.reader.impl.NaverCrawlerItemReader;
import com.drrr.reader.impl.TechobleCrawlerItemReader;
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
            case DEVOCEAN -> DevOceanCrawlerItemReader::new;
            case TECHOBLE -> TechobleCrawlerItemReader::new;
            case NHN_CLOUD -> NHNCloudCrawlerItemReader::new;
            case LINE -> LineCrawlerItemReader::new;
            case DEV_SISTERS -> DevSistersCrawlerItemReader::new;
            case BESPIN_GLOBAL -> BespinGlobalCrawlerItemReader::new;
        };
    }
}
