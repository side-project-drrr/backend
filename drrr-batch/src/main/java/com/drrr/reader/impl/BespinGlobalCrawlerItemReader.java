package com.drrr.reader.impl;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toUnmodifiableList;

import com.drrr.core.code.techblog.TechBlogCode;
import com.drrr.domain.ExternalBlogPost;
import com.drrr.domain.ExternalBlogPosts;
import com.drrr.reader.AbstractCrawlerPageItemReader;
import com.drrr.reader.CrawlerPageStrategy;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;


@Slf4j
public class BespinGlobalCrawlerItemReader extends AbstractCrawlerPageItemReader {

    private static final String TARGET_URL = "https://blog.bespinglobal.com/";
    private static final String PREFIX_URK = "https://blog.bespinglobal.com/post/";
    private static final TechBlogCode TECH_BLOG_CODE = TechBlogCode.BESPIN_GLOBAL;

    public BespinGlobalCrawlerItemReader(WebDriver webDriver) {
        super(CrawlerPageStrategy.PAGE, webDriver);
    }

    @Override
    protected ExternalBlogPosts executeCrawlerPage() {

        this.selectPage();

        final var result = this.webDriver.findElements(By.tagName("article"))
                .stream()
                .map(article -> {
                    var titleElement = article.findElement(By.className("entry-title"));
                    var aTag = titleElement.findElement(By.tagName("a"));
                    var title = aTag.getText();
                    var href = aTag.getAttribute("href");
                    var datetime = article.findElement(By.className("entry-date")).getAttribute("datetime");
                    var date = LocalDateTime.parse(datetime, DateTimeFormatter.ISO_OFFSET_DATE_TIME).toLocalDate();

                    var summary = article.findElement(By.className("entry-summary")).getText();

                    return ExternalBlogPost.builder()
                            .suffix(href.substring(PREFIX_URK.length()))
                            .link(href)
                            .title(title)
                            .postDate(date)
                            .summary(summary)
                            .code(TECH_BLOG_CODE)
                            .build();
                })
                .collect(collectingAndThen(toUnmodifiableList(), ExternalBlogPosts::new));
        log.info("{}", result);
        log.info("{}", result);
        return result;
    }


    @Override
    protected String getPageUrlByParameter(int page) {
        final var url = TARGET_URL + "page/" + page;
        log.info("crawler naver url: {}", url);
        return url;
    }

    @Override
    protected int getLastPage() {
        var is404Page = CrawlingUtils.existsByElement(() -> {
            this.webDriver.findElement(By.className("error404"));
        });
        // 404 페이지
        if (is404Page) {
            return this.getPage() - 1;
        }
        this.webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.className("nav-links")));

        return this.webDriver.findElement(By.className("nav-links"))
                .findElements(By.className("page-numbers"))
                .stream()
                .map(WebElement::getText)
                .filter(pageText -> pageText.startsWith("Page"))
                .map(text -> text.substring("Page".length()))
                .map(String::trim)
                .peek(t -> log.info("check {}", t))
                .filter(CrawlingUtils::isNumber)
                .mapToInt(Integer::parseInt)
                .max()
                .orElseThrow(IllegalStateException::new);
    }


}
