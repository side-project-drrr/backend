package com.drrr.reader.impl;

import static java.util.stream.Collectors.collectingAndThen;

import com.drrr.core.code.techblog.TechBlogCode;
import com.drrr.domain.ExternalBlogPost;
import com.drrr.domain.ExternalBlogPosts;
import com.drrr.reader.AbstractCrawlerPageItemReader;
import com.drrr.reader.CrawlerPageStrategy;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;


@Slf4j
public class DevSistersCrawlerItemReader extends AbstractCrawlerPageItemReader {

    private static final String TARGET_URL = "https://tech.devsisters.com/";

    public DevSistersCrawlerItemReader(WebDriver webDriver) {
        super(CrawlerPageStrategy.PAGE, webDriver);
    }

    @Override
    protected ExternalBlogPosts executeCrawlerPage() {
        this.selectPage();

        final var result = this.webDriver.findElements(By.tagName("article"))
                .stream().map(article -> {

                    final var href = article.findElement(By.tagName("a")).getAttribute("href");
                    final var title = article.findElement(By.tagName("h1")).getText();
                    final var summary = article.findElement(By.tagName("p")).getText();
                    final var date = article.findElement(By.tagName("time")).getText();
                    final var writer = article.findElement(By.tagName("span")).getText();

                    return ExternalBlogPost.builder()
                            .title(title)
                            .code(TechBlogCode.DEV_SISTERS)
                            .link(href)
                            .suffix(href.substring(TARGET_URL.length()))
                            .postDate(CrawlingLocalDatePatterns.PATTERN5.parse(date))
                            .summary(summary)
                            .author(writer)
                            .build();
                }).collect(collectingAndThen(Collectors.toUnmodifiableList(), ExternalBlogPosts::new));

        log.info("{}", result.posts());

        return result;
    }

    @Override
    protected String getPageUrlByParameter(int page) {
        final var url = TARGET_URL + "?page=" + page;
        log.info("crawler dev sisters url: {}", url);
        return url;
    }


    @Override
    protected int getLastPage() {
        this.webDriverWait.until(
                ExpectedConditions.visibilityOfElementLocated(By.className("Pagination-module--list--1OcIo")));
        return this.webDriver.findElement(By.className("Pagination-module--list--1OcIo"))
                .findElements(By.tagName("span"))
                .stream()
                .map(WebElement::getText)
                .mapToInt(Integer::parseInt)
                .max()
                .orElseThrow(IllegalArgumentException::new);
    }


}
