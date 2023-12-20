package com.drrr.reader.impl;

import com.drrr.core.code.techblog.TechBlogCode;
import com.drrr.domain.ExternalBlogPost;
import com.drrr.domain.ExternalBlogPosts;
import com.drrr.reader.AbstractCrawlerPageItemReader;
import com.drrr.reader.CrawlerPageStrategy;
import com.drrr.reader.impl.MarketKurlyItemReader.EmptyFinder;
import java.util.List;
import java.util.Optional;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
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
        this.webDriver.get("https://techblog.woowahan.com/");
    }

    static Optional<WebElement> EmptyElementFinder(WebDriver driver, By by) {
        try {
            return Optional.of(driver.findElement(by));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    protected ExternalBlogPosts executeCrawlerPage() {
        log.info("start crawler woowahan blog");
        setLastPage(getLastPage());
        WebElement postsElement = this.webDriver.findElement(By.className("posts"));

        webDriverWait.until(ExpectedConditions.and(
                ExpectedConditions.visibilityOfElementLocated(By.className("current")),
                ExpectedConditions.textToBePresentInElementLocated(By.className("current"), String.valueOf(getPage()))
        ));

        ExternalBlogPosts externalBlogPosts = new ExternalBlogPosts(
                postsElement.findElements(By.cssSelector(".item:not(.firstpaint)"))
                        .stream()
                        .map(webElement -> {
                            final WebElement aTag = webElement.findElement(By.tagName("a"));
                            final List<WebElement> spanTag = aTag.findElements(By.tagName("span"));
                            final List<WebElement> pTag = aTag.findElements(By.tagName("p"));
                            final String title = aTag.findElement(By.tagName("h1")).getText();
                            final String author = spanTag.get(1).getText();
                            final String summary = pTag.get(1).getText();
                            final String postDate = spanTag.get(0).getText();
                            final String thumbnail = EmptyFinder.get(
                                    () -> webElement.findElement(By.className("thumb")).findElement(By.tagName("img"))
                                            .getAttribute("src")).orElse("");
                            final String hrefContent = aTag.getAttribute("href");
                            final String suffix = Pattern.compile("\\d+")
                                    .matcher(hrefContent)
                                    .results()
                                    .map(MatchResult::group)
                                    .findFirst().get();

                            return ExternalBlogPost.builder()
                                    .link(PREFIX_URL + suffix)
                                    .suffix(suffix)
                                    .title(title)
                                    .author(author)
                                    .summary(summary)
                                    .thumbnailUrl(thumbnail)
                                    .postDate(CrawlingLocalDatePatterns.PATTERN3.parse(postDate))
                                    .code(CODE)
                                    .build();
                        }).toList());

        this.navigateToNextPage(super.getPage());

        return externalBlogPosts;
    }

    @Override
    protected int getLastPage() {
        String cssSelectorTarget = "a[class='page larger']";

        webDriverWait.until(ExpectedConditions.and(
                ExpectedConditions.visibilityOfElementLocated(By.className("current")),
                ExpectedConditions.textToBePresentInElementLocated(By.className("current"), String.valueOf(super.getPage()))
        ));

        Optional<WebElement> lastPageElement = EmptyElementFinder(webDriver, By.cssSelector(cssSelectorTarget));
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
        Optional<WebElement> targetPageElementOpt = EmptyElementFinder(webDriver, By.cssSelector("a[title='" + (page + 1) + " 쪽']"));
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
