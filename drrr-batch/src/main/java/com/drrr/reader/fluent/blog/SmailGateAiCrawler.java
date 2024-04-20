package com.drrr.reader.fluent.blog;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

import com.drrr.core.ProxyTechBlogReader;
import com.drrr.core.code.techblog.TechBlogCode;
import com.drrr.domain.ExternalBlogPost;
import com.drrr.domain.ExternalBlogPosts;
import com.drrr.fluent.cralwer.core.ContentsLoader;
import com.drrr.fluent.cralwer.core.ContentsReader;
import com.drrr.fluent.cralwer.core.SimpleContentsLoader;
import com.drrr.fluent.cralwer.core.SinglePage;
import com.drrr.fluent.cralwer.core.SinglePage.Mode;
import com.drrr.fluent.cralwer.core.WebDriverPool;
import com.drrr.reader.AbstractCrawlerPageItemReader.CrawlingLocalDatePatterns;
import com.drrr.reader.AbstractCrawlerPageItemReader.CrawlingUtils;
import com.drrr.reader.fluent.PageItemReader;
import com.drrr.reader.fluent.TechBlogReader;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Slf4j
@Configuration
public class SmailGateAiCrawler {

    private static final String BASE_URL = "https://smilegate.ai/recent/";
    private static final String PREFIX = "https://smilegate.ai/";
    private static final TechBlogCode CODE = TechBlogCode.SMAIL_GATE_AI;


    @Bean
    TechBlogReader smailGateAiReader(WebDriverPool webDriverPool) {
        return new ProxyTechBlogReader(() -> {
            var page = SinglePage.<ExternalBlogPosts>builder()
                    .mode(Mode.ONCE)
                    .webDriver(webDriverPool.borrow())
                    .webDriverCleaner(webDriverPool::returnObject)
                    .singlePageInitializer(() -> BASE_URL)
                    .contentsLoader(contentsLoader())
                    .contentsReader(contentsReader())
                    // .after(data -> log.info("{}", data))
                    .build();

            return new PageItemReader(page, CODE);
        }, CODE);
    }

    private ContentsReader<ExternalBlogPosts> contentsReader() {

        return webDriver -> {
            ((JavascriptExecutor) webDriver).executeScript(
                    "[...document.querySelectorAll(\".dt-css-grid .hidden article\")].forEach(element => element.classList.add(\"show\"))");
            return Stream.concat(
                    webDriver.findElements(By.cssSelector(".dt-css-grid .visible article")).stream().map(this::parse),
                    webDriver.findElements(By.cssSelector(".dt-css-grid .hidden article")).parallelStream()
                            .map(this::hiddenParse)
            ).collect(collectingAndThen(toList(), ExternalBlogPosts::new));
        };

    }

    private ExternalBlogPost hiddenParse(WebElement webElement) {
        var thumbnailElement = CrawlingUtils.findByElement(() -> webElement.findElement(By.tagName("img")));

        //data-src
        var date = webElement.getAttribute("data-date");
        var titleElement = webElement.findElement(By.cssSelector(".entry-title a"));

        var summaryElement = CrawlingUtils.findByElement(
                () -> webElement.findElement(By.cssSelector(".entry-excerpt p")));
        log.info("{}", webElement.getText());

        var link = CrawlingUtils.urlDecode(titleElement.getAttribute("href"));
        return ExternalBlogPost
                .builder()
                .link(link)
                .suffix(link.substring(PREFIX.length()))
                .code(CODE)
                .title(webElement.getAttribute("data-name"))
                .postDate((CrawlingLocalDatePatterns.PATTERN10.parse(date)))
                .summary(summaryElement.map(WebElement::getText).orElse(null))
                .thumbnailUrl(thumbnailElement.map(element -> element.getAttribute("data-src")).orElse(null))
                .build();
    }

    private ExternalBlogPost parse(WebElement webElement) {
        var thumbnailElement = CrawlingUtils.findByElement(() -> webElement.findElement(By.tagName("img")));
        var date = webElement.getAttribute("data-date");
        var titleElement = webElement.findElement(By.cssSelector(".entry-title a"));
        var summaryElement = webElement.findElement(By.cssSelector(".entry-excerpt p"));

        var link = CrawlingUtils.urlDecode(titleElement.getAttribute("href"));
        return ExternalBlogPost
                .builder()
                .link(link)
                .suffix(link.substring(PREFIX.length()))
                .code(CODE)
                .title(titleElement.getText())
                .summary(summaryElement.getText())
                .postDate((CrawlingLocalDatePatterns.PATTERN10.parse(date)))
                .thumbnailUrl(thumbnailElement.map(element -> element.getAttribute("src")).orElse(null))
                .build();
    }

    private ContentsLoader contentsLoader() {
        return new SimpleContentsLoader(By.className("dt-css-grid"));
    }
}
