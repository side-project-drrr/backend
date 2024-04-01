package com.drrr.reader;

import com.drrr.core.code.techblog.TechBlogCode;
import com.drrr.domain.ExternalBlogPosts;
import com.drrr.fluent.cralwer.core.WebDriverPool;
import com.drrr.reader.fluent.FluentCrawlerProvider;
import com.drrr.reader.impl.BespinGlobalCrawlerItemReader;
import com.drrr.reader.impl.DaangnCrawlerItemReader;
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
import java.util.Objects;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.WebDriver;
import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CrawlerItemReaderFactory {

    private final WebDriverPool webDriverPool;
    private final FluentCrawlerProvider fluentCrawlerProvider;

    public ItemReader<ExternalBlogPosts> createItemReader(TechBlogCode code) {
        var itemReader = fluentCrawlerProvider.getItemReader(code);

        if (Objects.isNull(itemReader)) {
            return this.findItemReaderBy(code).apply(webDriverPool.borrow());
        }

        return itemReader;
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
            case DAANGN -> DaangnCrawlerItemReader::new;
            case SARAMIN -> null;
            case SQUARE_LAB -> null;
            case DRAMANCOMPANY -> null;
            case KAKAO_PAY -> null;
            case SMAIL_GATE_AI -> null;
        };
    }


}
