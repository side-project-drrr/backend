package com.drrr.reader.impl;

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
public class LineCrawlerItemReader extends AbstractCrawlerPageItemReader {

    private static final String TARGET_URL = "https://techblog.lycorp.co.jp/ko";

    private static final TechBlogCode techBlogCode = TechBlogCode.LINE;

    public LineCrawlerItemReader(WebDriver webDriver) {
        super(CrawlerPageStrategy.PAGE, webDriver);
    }

    @Override
    protected ExternalBlogPosts executeCrawlerPage() {
        //list_post
        this.selectPage();

        final var result = this.webDriver.findElement(By.className("list_post"))
                .findElements(By.tagName("li"))
                .stream()
                .map((webElement -> {
                    final var href = webElement.findElement(By.tagName("a")).getAttribute("href");
                    final var image = webElement.findElement(By.className("thumbnail"))
                            .findElement(By.tagName("img"))
                            .getAttribute("src");

                    final var title = webElement.findElement(By.tagName("h2")).getText();
                    final var date = webElement.findElement(By.className("update")).getText()
                            .replace("Date:", "")
                            .replace("\n", "");

                    return ExternalBlogPost.builder()
                            .title(title)
                            .thumbnailUrl(image)
                            .suffix(href.substring(TARGET_URL.length()))
                            .code(techBlogCode)
                            .postDate(CrawlingLocalDatePatterns.PATTERN1.parse(date))
                            .link(href)
                            .build();
                }))
                .collect(Collectors.collectingAndThen(Collectors.toList(), ExternalBlogPosts::new));

        log.info("{}", result.posts());
        return result;
    }

    @Override
    protected String getPageUrlByParameter(int page) {
        final var url = TARGET_URL + "?page=" + page;
        log.info("crawler line url: {}", url);
        return url;
    }


    @Override
    protected int getLastPage() {
        this.webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.className("pagination")));
        return this.webDriver.findElements(By.className("pagination"))
                .stream()
                .map(webElement -> webElement.findElement(By.className("page")))
                .map(WebElement::getText)
                .mapToInt(Integer::parseInt)
                .max()
                .orElseThrow(IllegalArgumentException::new);
    }


}
