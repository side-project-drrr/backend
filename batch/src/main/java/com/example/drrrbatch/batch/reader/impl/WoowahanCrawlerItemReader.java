package com.example.drrrbatch.batch.reader.impl;

import static org.openqa.selenium.support.ui.ExpectedConditions.textToBe;

import com.example.drrrbatch.batch.domain.ExternalBlogPost;
import com.example.drrrbatch.batch.domain.ExternalBlogPosts;
import com.example.drrrbatch.batch.reader.AbstractCrawlerPageItemReader;
import com.example.drrrbatch.batch.reader.CrawlerPageStrategy;

import com.example.drrrbatch.batch.reader.impl.MarketKurlyItemReader.EmptyFinder;
import com.example.drrrbatch.batch.vo.TechBlogCode;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import javax.swing.text.html.Option;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

@Slf4j
public class WoowahanCrawlerItemReader extends AbstractCrawlerPageItemReader implements StepExecutionListener {
    private static final TechBlogCode CODE = TechBlogCode.WOOWAHAN;
    private static final String PREFIX_URL = "https://techblog.woowahan.com/";


    public WoowahanCrawlerItemReader(WebDriver webDriver) {
        super(CrawlerPageStrategy.PAGE, webDriver);
        this.webDriver.get("https://techblog.woowahan.com/");
    }


    @Override
    protected ExternalBlogPosts executeCrawlerPage() {
        log.info("start crawler woowahan blog");
        setLastPage();
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
        if(lastPageElement.isEmpty()){
            cssSelectorTarget = "a[class='page smaller']";
        }

        return this.webDriver.findElements(By.cssSelector(cssSelectorTarget))
                .stream()
                .map(webElement -> webElement.getText())
                .mapToInt(Integer::parseInt)
                .max()
                .orElseThrow(IllegalArgumentException::new);
    }
    static Optional<WebElement> EmptyElementFinder(WebDriver driver, By by) {
        try {
            return Optional.of(driver.findElement(by));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
    @Override
    protected void navigateToNextPage(int page) {
        Optional<WebElement> targetPageElementOpt = EmptyElementFinder(webDriver, By.cssSelector("a[title='" + (page+1) + " 쪽']"));
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
