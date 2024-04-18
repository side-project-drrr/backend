package com.drrr.reader.impl;

import com.drrr.core.code.techblog.TechBlogCode;
import com.drrr.domain.ExternalBlogPost;
import com.drrr.domain.ExternalBlogPosts;
import com.drrr.reader.AbstractCrawlerPageItemReader;
import com.drrr.reader.CrawlerPageStrategy;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

@Slf4j
public class WoowahanCrawlerItemReader extends AbstractCrawlerPageItemReader {
    private static final TechBlogCode CODE = TechBlogCode.WOOWAHAN;
    private static final String PREFIX_URL = "https://techblog.woowahan.com/";


    public WoowahanCrawlerItemReader(WebDriver webDriver) {
        super(CrawlerPageStrategy.PAGE, webDriver);
        this.webDriver.get(PREFIX_URL);
    }


    @Override
    protected ExternalBlogPosts executeCrawlerPage() {
        log.info("start crawler woowahan blog");
        this.selectPage();

        WebElement postsElement = this.webDriver.findElement(By.className("post-list"));

        this.webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.className("wp-pagenavi")));

        log.info("{}", postsElement.findElement(By.className("post-item")));
        final var externalBlogPosts = postsElement.findElements(By.cssSelector(".post-item:not(.firstpaint)"))
                .stream()
                .map(webElement -> {

                    final var classFinder = findTagToText(webElement);
                    final var title = classFinder.apply("post-title");
                    final var author = classFinder.apply("post-author-name");
                    final var summary = classFinder.apply("post-excerpt");
                    final var postDate = classFinder.apply("post-author-date");
                    final var href = webElement.findElements(By.tagName("a")).get(1).getAttribute("href");

                    return ExternalBlogPost.builder()
                            .link(href)
                            .suffix(href.substring(PREFIX_URL.length()).replace("/", ""))
                            .title(title)
                            .author(author)
                            .summary(summary)
                            .postDate(CrawlingLocalDatePatterns.PATTERN3.parse(postDate))
                            .code(CODE)
                            .build();
                }).collect(Collectors.collectingAndThen(Collectors.toList(), ExternalBlogPosts::new));

        log.info("{}", externalBlogPosts.posts().size());
        return externalBlogPosts;
    }

    private Function<String, String> findTagToText(WebElement element) {
        return (className) -> element.findElement(By.className(className)).getText();
    }


    boolean isNumber(String text) {
        try {
            Integer.parseInt(text);
            return true;
        } catch (NumberFormatException exception) {
            return false;
        }
    }


    @Override
    protected String getPageUrlByParameter(int page) {
        final var url = "https://techblog.woowahan.com/?paged=" + page;
        log.info("crawler woowahan url: {}", url);
        return url;
    }

    @Override
    protected int getLastPage() {
        this.webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.className("wp-pagenavi")));

        return this.webDriver.findElement(By.className("wp-pagenavi"))
                .findElements(By.tagName("a"))
                .stream()
                .map(WebElement::getText)
                .filter(this::isNumber)
                .mapToInt(Integer::parseInt)
                .max()
                .orElseThrow(IllegalArgumentException::new);
    }
}
