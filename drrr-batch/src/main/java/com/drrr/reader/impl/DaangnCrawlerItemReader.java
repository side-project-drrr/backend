package com.drrr.reader.impl;

import com.drrr.core.code.techblog.TechBlogCode;
import com.drrr.domain.ExternalBlogPost;
import com.drrr.domain.ExternalBlogPosts;
import com.drrr.reader.AbstractCrawlerPageItemReader;
import com.drrr.reader.CrawlerPageStrategy;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;


@Slf4j
public class DaangnCrawlerItemReader extends AbstractCrawlerPageItemReader {


    private static final String TARGET_URL = "https://medium.com/daangn/";
    private static final TechBlogCode TECH_BLOG_CODE = TechBlogCode.DAANGN;

    public DaangnCrawlerItemReader(WebDriver webDriver) {
        super(CrawlerPageStrategy.SINGLE_PAGE, webDriver);
    }

    private static ExternalBlogPost findFirstPost(WebElement content) {
        var area = content.findElements(By.xpath("./child::*"));

        var aTag = area.get(0).findElement(By.tagName("a"));
        var link = CrawlingUtils.removeParameterWithUrl(CrawlingUtils.urlDecode(aTag.getAttribute("href")));

        var postContent = area.get(1).findElements(By.xpath("./child::*"));
        var summary = postContent.get(0)
                .getText();
        var title = content.findElement(By.tagName("h3")).getText();
        var datetime = postContent.get(1)
                .findElement(By.tagName("time"))
                .getAttribute("datetime")
                .substring(0, 10);

        var date = CrawlingLocalDatePatterns.PATTERN5.parse(datetime);

        return ExternalBlogPost.builder()
                .summary(summary)
                .title(title)
                .postDate(date)
                .thumbnailUrl(CrawlingUtils.removeImgBracket(aTag.getCssValue("background-image")))
                .link(link)
                .code(TECH_BLOG_CODE)
                .suffix(link.substring(TARGET_URL.length()))
                .build();
    }

    private static Stream<ExternalBlogPost> findSecondaryPost(WebElement row) {
        return row.findElements(By.xpath("./child::*"))
                .stream()
                .map(content -> {
                    var post = content.findElements(By.xpath("./child::*"));
                    var aTag = post.get(0).findElement(By.tagName("a"));
                    var link = CrawlingUtils.removeParameterWithUrl(CrawlingUtils.urlDecode(aTag.getAttribute("href")));

                    var imageUrl = aTag.getCssValue("background-image");
                    var postContent = post.get(1).findElements(By.xpath("./child::*"));
                    var summary = postContent.get(0)
                            .getText();
                    var title = content.findElement(By.tagName("h3")).getText();
                    var datetime = postContent.get(1)
                            .findElement(By.tagName("time"))
                            .getText();

                    if (datetime.length() <= 5) {
                        datetime += ", " + LocalDateTime.now().getYear();
                    }
                    var date = CrawlingLocalDatePatterns.PATTERN6.parse(datetime);

                    return ExternalBlogPost.builder()
                            .summary(summary)
                            .title(title)
                            .postDate(date)
                            .thumbnailUrl(CrawlingUtils.removeImgBracket(imageUrl))
                            .link(link)
                            .code(TECH_BLOG_CODE)
                            .suffix(link.substring(TARGET_URL.length()))
                            .build();
                });
    }

    @Override
    protected ExternalBlogPosts executeCrawlerPage() {
        log.info("start crawler danngn");

        this.webDriver.get(TARGET_URL);

        this.driverWait(By.tagName("nav"));

        var urls = webDriver.findElement(By.tagName("nav"))
                .findElements(By.tagName("li"))
                .stream()
                .takeWhile(li -> !Objects.equals(li.getText(), "IT스타트업"))
                .map((li) -> li.findElement(By.tagName("a")).getAttribute("href"))
                .toList();

        log.info("{}", urls);

        var result = urls.stream().flatMap(url -> {
                    this.webDriver.get(url);

                    this.driverWait(By.tagName("section"));
                    this.webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("section")));

                    var section = this.webDriver.findElement(By.tagName("section"));

                    return Stream.concat(
                            Stream.of(section.findElement(By.className("u-marginTop30")))
                                    .map(DaangnCrawlerItemReader::findFirstPost),
                            section.findElements(By.className("u-marginTop15"))
                                    .stream()
                                    .flatMap(DaangnCrawlerItemReader::findSecondaryPost));
                })
                .toList();

        log.info("{}", result);

        return new ExternalBlogPosts(result);
    }
}
