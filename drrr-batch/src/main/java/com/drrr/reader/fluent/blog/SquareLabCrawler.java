package com.drrr.reader.fluent.blog;


import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

import com.drrr.core.code.techblog.TechBlogCode;
import com.drrr.domain.ExternalBlogPost;
import com.drrr.domain.ExternalBlogPosts;
import com.drrr.fluent.cralwer.core.ContentsLoader;
import com.drrr.fluent.cralwer.core.ContentsReader;
import com.drrr.fluent.cralwer.core.GenericParallelPages;
import com.drrr.fluent.cralwer.core.PaginationReader;
import com.drrr.fluent.cralwer.core.PaginationReader.PaginationInformation;
import com.drrr.fluent.cralwer.core.ParallelPageInitializer.BasePageUrls;
import com.drrr.fluent.cralwer.core.SimpleContentsLoader;
import com.drrr.fluent.cralwer.core.WebDriverPool;
import com.drrr.reader.AbstractCrawlerPageItemReader.CrawlingLocalDatePatterns;
import com.drrr.reader.fluent.ParallelPageItemReader;
import com.drrr.reader.fluent.TechBlogReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class SquareLabCrawler {
    private static final TechBlogCode CODE = TechBlogCode.SQUARE_LAB;
    private static final String BASE_URL = "https://squarelab.co/blog/";

    @Bean
    public TechBlogReader squareLabPageReader(WebDriverPool webDriverPool) {

        var pages = GenericParallelPages.<ExternalBlogPosts>builder()
                .webDriverPool(webDriverPool)
                .parallelCount(3)
                .pageInitializer(() -> new BasePageUrls(
                        BASE_URL,
                        (page) -> BASE_URL + "?page=" + page
                ))
                .paginationReader(paginationReader())
                .contentsLoader(contentsLoader())
                .contentsReader(contentsReader())
                .after(data -> log.info("{}", data))
                .build();
        return new ParallelPageItemReader(pages, CODE);
    }

    ContentsReader<ExternalBlogPosts> contentsReader() {
        return webDriver -> {
            log.info("{}", webDriver.findElements(By.className("post-wrap")).size());
            var r = webDriver.findElements(By.className("post-wrap"))
                    .stream()
                    .peek(postWrap -> log.info("{}", postWrap.getAttribute("style")))
                    .filter(postWrap -> !postWrap.getAttribute("style").isBlank())
                    .map(this::parseBy)
                    .collect(collectingAndThen(toList(), ExternalBlogPosts::new));

            log.info("check {}", r);
            return r;
        };
    }

    ExternalBlogPost parseBy(WebElement postElement) {
        log.info("parse");

        var linkElement = postElement.findElement(By.cssSelector(".post-txt a"));
        var thumbnail = postElement.findElement(By.className("img-responsive")).getAttribute("src");
        var smalls = postElement.findElements(By.cssSelector(".post-txt .author small"));
        var url = linkElement.getAttribute("href");
        log.info("{}", smalls.stream().map(WebElement::getText).toList());
        var title = linkElement.getText();
        var author = smalls.get(0).getText();

        var postDate = CrawlingLocalDatePatterns.PATTERN8.parse(smalls.get(2).getText());
        var summary = postElement.findElement(By.cssSelector(".post-txt .excerpt")).getText();

        return ExternalBlogPost.builder()
                .title(title)
                .link(url)
                .author(author)
                .suffix(url.substring(BASE_URL.length()))
                .summary(summary)
                .code(CODE)
                .postDate(postDate)
                .thumbnailUrl(thumbnail)
                .build();

    }

    ContentsLoader contentsLoader() {
        return webDriverWait -> new SimpleContentsLoader(By.className("blog-row"));
    }

    PaginationReader paginationReader() {
        return webDriver -> PaginationInformation.lastPage(webDriver.findElement(By.id("pagination"))
                .findElements(By.tagName("a"))
                .stream()
                .map(WebElement::getText)
                .map(Integer::parseInt)
                .reduce(Integer.MIN_VALUE, Math::max));
    }
}
