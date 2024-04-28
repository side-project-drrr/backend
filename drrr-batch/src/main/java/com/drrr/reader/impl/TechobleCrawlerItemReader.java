package com.drrr.reader.impl;

import com.drrr.core.code.techblog.TechBlogCode;
import com.drrr.domain.ExternalBlogPost;
import com.drrr.domain.ExternalBlogPosts;
import com.drrr.reader.AbstractCrawlerPageItemReader;
import com.drrr.reader.CrawlerPageStrategy;
import com.drrr.reader.impl.MarketKurlyItemReader.EmptyFinder;
import java.time.LocalDate;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;


@Slf4j
public class TechobleCrawlerItemReader extends AbstractCrawlerPageItemReader {
    private static final String TARGET_URL = "https://tecoble.techcourse.co.kr/";
    private static final TechBlogCode TECH_BLOG_CODE = TechBlogCode.TECHOBLE;

    public TechobleCrawlerItemReader(WebDriver webDriver) {
        super(CrawlerPageStrategy.SINGLE_PAGE, webDriver);
    }

    @Override
    protected ExternalBlogPosts executeCrawlerPage() {
        this.webDriver.get(TARGET_URL);

        this.webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("site-main")));

        var result = webDriver.findElements(By.tagName("article"))
                .stream()
                .map(card -> {

                    var imageSrc = EmptyFinder.get(() -> card.findElement(By.className("post-card-image"))
                            .findElement(By.tagName("picture"))
                            .findElement(By.tagName("img"))
                            .getAttribute("src"));
                    var content = card.findElement(By.className("post-card-content"));
                    var footer = content.findElement(By.className("post-card-meta"));

                    var author = content.findElement(By.className("post-card-byline-content"))
                            .findElement(By.tagName("a"));
                    var link = content.findElement(By.className("post-card-content-link")).getAttribute("href");
                    var title = content.findElement(By.className("post-card-title"));
                    var summary = content.findElement(By.className("post-card-excerpt"));
                    var time = footer.findElement(By.tagName("time")).getAttribute("datetime");

                    return ExternalBlogPost.builder()
                            .code(TECH_BLOG_CODE)
                            .title(title.getText())
                            .suffix(link.substring(TARGET_URL.length()))
                            .link(link)
                            .author(author.getText())
                            .summary(summary.getText())
                            .thumbnailUrl(imageSrc.orElse(""))
                            .postDate(LocalDate.parse(time))
                            .build();
                })
                .peek(data -> log.info("{}", data))
                .toList();
        return new ExternalBlogPosts(result);
    }
}
