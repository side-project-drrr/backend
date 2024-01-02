package com.drrr.reader.impl;

import com.drrr.core.code.techblog.TechBlogCode;
import com.drrr.domain.ExternalBlogPost;
import com.drrr.domain.ExternalBlogPosts;
import com.drrr.reader.AbstractCrawlerPageItemReader;
import com.drrr.reader.CrawlerPageStrategy;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.springframework.batch.core.StepExecutionListener;

@Slf4j
public class WoowahanCrawlerItemReader extends AbstractCrawlerPageItemReader implements StepExecutionListener {
    private static final TechBlogCode CODE = TechBlogCode.WOOWAHAN;
    private static final String PREFIX_URL = "https://techblog.woowahan.com/";


    public WoowahanCrawlerItemReader(WebDriver webDriver) {
        super(CrawlerPageStrategy.PAGE, webDriver);
        this.webDriver.get(PREFIX_URL);
    }

    static Optional<WebElement> emptyElementFinder(WebDriver driver, By by) {
        try {
            return Optional.of(driver.findElement(by));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    protected ExternalBlogPosts executeCrawlerPage() {
        log.info("start crawler woowahan blog");

        setLastPage(this.getLastPage());

        WebElement postsElement = this.webDriver.findElement(By.className("post-list"));

        webDriverWait.until(ExpectedConditions.and(
                ExpectedConditions.visibilityOfElementLocated(By.className("current")),
                ExpectedConditions.textToBePresentInElementLocated(By.className("current"), String.valueOf(getPage()))
        ));

        log.info("{}", postsElement.findElement(By.className("post-item")));

        ExternalBlogPosts externalBlogPosts = postsElement.findElements(By.cssSelector(".post-item:not(.firstpaint)"))
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
        this.navigateToNextPage(super.getPage());

        return externalBlogPosts;
    }

    private Function<String, String> findTagToText(WebElement element) {
        return (className) -> element.findElement(By.className(className)).getText();
    }

    @Override
    protected int getLastPage() {
        String cssSelectorTarget = "a[class='page larger']";

        webDriverWait.until(ExpectedConditions.and(
                ExpectedConditions.visibilityOfElementLocated(By.className("current")),
                ExpectedConditions.textToBePresentInElementLocated(By.className("current"),
                        String.valueOf(super.getPage()))
        ));

        Optional<WebElement> lastPageElement = emptyElementFinder(webDriver, By.cssSelector(cssSelectorTarget));
        if (lastPageElement.isEmpty()) {
            cssSelectorTarget = "a[class='page smaller']";
        }

        return this.webDriver.findElements(By.cssSelector(cssSelectorTarget))
                .stream()
                .map(WebElement::getText)
                .mapToInt(Integer::parseInt)
                .max()
                .orElseThrow(IllegalArgumentException::new);
    }

    @Override
    protected void navigateToNextPage(int page) {
        Optional<WebElement> targetPageElementOpt = emptyElementFinder(webDriver,
                By.cssSelector("a[title='" + (page + 1) + " 쪽']"));
        WebElement targetPageElement = targetPageElementOpt.orElse(null);

        // 요소를 클릭합니다.
        if (targetPageElement != null) {
            targetPageElement.click();
            webDriverWait.until(ExpectedConditions.and(
                    ExpectedConditions.visibilityOfElementLocated(By.className("current")),
                    ExpectedConditions.textToBePresentInElementLocated(By.className("current"),
                            String.valueOf(page))
            ));
        }
    }


}
