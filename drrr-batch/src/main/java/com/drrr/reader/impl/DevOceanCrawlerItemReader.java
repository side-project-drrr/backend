package com.drrr.reader.impl;

import com.drrr.core.code.techblog.TechBlogCode;
import com.drrr.domain.ExternalBlogPost;
import com.drrr.domain.ExternalBlogPosts;
import com.drrr.reader.AbstractCrawlerPageItemReader;
import com.drrr.reader.CrawlerPageStrategy;
import com.drrr.reader.impl.MarketKurlyItemReader.EmptyFinder;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;


@Slf4j
public class DevOceanCrawlerItemReader extends AbstractCrawlerPageItemReader {

    private static final String TARGET_URL = "https://devocean.sk.com/blog/sub/index"
            + ".do?ID=&searchData=&page=%d&subIndex=최신기술블로그";

    private static final String PREFIX_URL = "https://devocean.sk.com/blog/techBoardDetail"
            + ".do?ID=165601&boardType=techBlog";

    private static final String URL_FORMAT = "https://devocean.sk.com/blog/techBoardDetail"
            + ".do?ID=%s";


    public DevOceanCrawlerItemReader(WebDriver webDriver) {
        super(CrawlerPageStrategy.PAGE, webDriver);
    }

    @Override
    protected ExternalBlogPosts executeCrawlerPage() {
        this.selectPage();
        try {
            this.webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.className("sec-area-list01")));
        } catch (TimeoutException timeoutException) {
            EmptyFinder.get(() -> webDriver.findElement(By.className("sec-area-list01")))
                    .ifPresent((__) -> {
                        throw new RuntimeException();
                    });
            return null;
        }
        return this.webDriver.findElement(By.className("sec-area-list01"))
                .findElements(By.tagName("li"))
                .stream().map(li -> {
                    final var title = li.findElement(By.className("sec-cont"))
                            .findElement(By.className("title"))
                            .getText();

                    log.info("{}", title);
                    final var description = li.findElement(By.className("desc"));
                    final var author = li.findElement(By.className("author")).findElement(By.tagName("em"));
                    final var date = CrawlingUtils.findByElement(() -> li.findElement(By.className("date")));
                    final var img = li.findElement(By.className("sec-img")).findElement(By.tagName("img"));

                    var postId = li.findElement(By.className("favorites")).getAttribute("data-board-id");

                    return ExternalBlogPost.builder()
                            .link(String.format(URL_FORMAT, postId))
                            .suffix(postId)
                            .title(title)
                            .author(author.getText())// naver는 작성자가 없음
                            .summary(description.getText())
                            .thumbnailUrl(img.getAttribute("src"))
                            .postDate(date.map(WebElement::getText)
                                    .map(CrawlingLocalDatePatterns.PATTERN4::parse)
                                    .orElse(null))
                            .code(TechBlogCode.DEVOCEAN)
                            .build();
                })
                .collect(Collectors.collectingAndThen(Collectors.toUnmodifiableList(), ExternalBlogPosts::new));
    }

    @Override
    protected String getPageUrlByParameter(int page) {
        var url = String.format(TARGET_URL, page);
        log.info("crawler devocean url: {}", url);
        return url;
    }


    @Override
    protected int getLastPage() {
        try {
            this.webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.className("sec-area-paging")));
        } catch (TimeoutException timeoutException) {
            EmptyFinder.get(() -> webDriver.findElement(By.className("sec_area_paging")))
                    .ifPresent((__) -> {
                        throw new RuntimeException();
                    });

            return this.getPage() - 1;
        }

        return this.webDriver.findElement(By.className("sec-area-paging"))
                .findElements(By.tagName("a"))
                .stream()
                .map(webElement -> webElement.getAttribute("onClick"))
                .filter(Objects::nonNull)
                .filter(data -> data.length() > "goPage();".length())
                .map(data -> data.substring("goPage(".length()))
                .map(data -> data.substring(0, data.indexOf(')')))
                .peek(log::info)
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
