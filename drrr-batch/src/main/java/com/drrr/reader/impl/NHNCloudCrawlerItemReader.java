package com.drrr.reader.impl;

import com.drrr.core.code.techblog.TechBlogCode;
import com.drrr.domain.ExternalBlogPost;
import com.drrr.domain.ExternalBlogPosts;
import com.drrr.reader.AbstractCrawlerPageItemReader;
import com.drrr.reader.CrawlerPageStrategy;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;


@Slf4j
public class NHNCloudCrawlerItemReader extends AbstractCrawlerPageItemReader {
    private static final String URL_FORMAT = "https://meetup.nhncloud.com/?page=%d";

    private final String urlPattern = "url\\('([^']+)'\\)";

    // 패턴 컴파일

    public NHNCloudCrawlerItemReader(WebDriver webDriver) {
        super(CrawlerPageStrategy.PAGE, webDriver);
    }

    @Override
    protected ExternalBlogPosts executeCrawlerPage() {
        this.selectPage();

        this.webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.className("section_inner")));
        var result = this.webDriver.findElement(By.className("section_inner"))
                .findElements(By.tagName("li"))
                .stream().map(card -> {

                    var thumbnailUrl = card.findElement(By.className("img_area")).getAttribute("style");
                    var title = card.findElement(By.tagName("h3"));
                    var link = card.findElement(By.tagName("a"));
                    var content = card.findElement(By.tagName("p"));
                    var date = card.findElement(By.className("date"));

                    final var firstSliceUrl = thumbnailUrl.substring("background-image: url(".length() + 1);
                    final var lastUrl = firstSliceUrl.substring(0, firstSliceUrl.length() - 3);

                    return ExternalBlogPost.builder()
                            .thumbnailUrl(lastUrl)
                            .title(title.getText())
                            .code(TechBlogCode.NHN_CLOUD)
                            .summary(content.getText())
                            .postDate(CrawlingLocalDatePatterns.PATTERN1.parse(date.getText().split(" ")[1]))
                            .suffix(link.getAttribute("ng-href"))
                            .link(link.getAttribute("href"))
                            .build();
                }).toList();
        return new ExternalBlogPosts(result);
    }

    @Override
    protected String getPageUrlByParameter(int page) {
        final var url = String.format(URL_FORMAT, page);
        log.info("NHN 클라우드 크롤링 {}", url);

        return url;

    }

    @Override
    protected int getLastPage() {
        this.webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.className("tui-pagination")));

        return this.webDriver.findElement(By.className("tui-pagination"))
                .findElements(By.className("tui-page-btn"))
                .stream()
                .map(WebElement::getText)
                .filter(this::isNumber)
                .mapToInt(Integer::parseInt)
                .max()
                .orElseThrow(IllegalArgumentException::new);


    }

    boolean isNumber(String text) {
        try {
            Integer.parseInt(text);
            return true;
        } catch (NumberFormatException exception) {
            return false;
        }
    }
}
