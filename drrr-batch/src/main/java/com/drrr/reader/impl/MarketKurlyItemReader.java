package com.drrr.reader.impl;

import com.drrr.core.code.techblog.TechBlogCode;
import com.drrr.domain.ExternalBlogPost;
import com.drrr.domain.ExternalBlogPosts;
import com.drrr.reader.AbstractCrawlerPageItemReader;
import com.drrr.reader.CrawlerPageStrategy;
import java.util.Optional;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

@Slf4j
public class MarketKurlyItemReader extends AbstractCrawlerPageItemReader {
    private static final String BLOG_PREFIX = "https://helloworld.kurly.com/blog";
    private static final TechBlogCode code = TechBlogCode.MARKET_KURLY;

    public MarketKurlyItemReader(WebDriver webDriver) {
        super(CrawlerPageStrategy.SINGLE_PAGE, webDriver);
    }

    @Override
    protected ExternalBlogPosts executeCrawlerPage() {
        log.info("run market kurly crawler");
        this.webDriver.get("https://helloworld.kurly.com/");
        this.webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.className("post-list")));

        var crawlerResult = webDriver.findElement(By.className("post-list"))
                .findElements(By.className("post-card"))
                .stream().map(webElement -> {
                    var postLink = EmptyFinder.get(() -> webElement.findElement(By.tagName("a")))
                            .orElseThrow(IllegalArgumentException::new);
                    var postTitle = EmptyFinder.get(() -> postLink.findElement(By.className("post-title")).getText())
                            .get();
                    var postSummary = EmptyFinder.get(
                            () -> postLink.findElement(By.className("title-summary")).getText()).orElse("");
                    var postMeta = EmptyFinder.get(() -> webElement.findElement(By.className("post-meta"))).get();
                    var postAuthor = EmptyFinder.get(() -> postMeta.findElement(By.className("post-autor")).getText())
                            .orElse("");
                    var postDate = EmptyFinder.get(() -> postMeta.findElement(By.className("post-date")).getText())
                            .orElse("");

                    return ExternalBlogPost.builder()
                            .title(postTitle)
                            .summary(postSummary)
                            .author(postAuthor)
                            .postDate(CrawlingLocalDatePatterns.PATTERN2.parse(postDate))
                            .link(postLink.getAttribute("href"))
                            .suffix(postLink.getAttribute("href").substring(BLOG_PREFIX.length() + 1))
                            .code(code)
                            .build();
                }).toList();

        return new ExternalBlogPosts(crawlerResult);
    }

    static class EmptyFinder {
        static <R> Optional<R> get(Supplier<R> result) {
            try {
                return Optional.of(result.get());
            } catch (Exception e) {
                return Optional.empty();
            }
        }
    }
}
