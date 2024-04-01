package com.drrr.reader.fluent.blog;

import static java.util.stream.Collectors.collectingAndThen;
import static org.openqa.selenium.By.className;
import static org.openqa.selenium.By.cssSelector;

import com.drrr.core.code.techblog.TechBlogCode;
import com.drrr.domain.ExternalBlogPost;
import com.drrr.domain.ExternalBlogPosts;
import com.drrr.fluent.cralwer.core.ContentsLoader;
import com.drrr.fluent.cralwer.core.ContentsReader;
import com.drrr.fluent.cralwer.core.GenericParallelPages;
import com.drrr.fluent.cralwer.core.PaginationReader;
import com.drrr.fluent.cralwer.core.PaginationReader.PaginationInformation;
import com.drrr.fluent.cralwer.core.ParallelPageInitializer;
import com.drrr.fluent.cralwer.core.ParallelPageInitializer.BasePageUrls;
import com.drrr.fluent.cralwer.core.SimpleContentsLoader;
import com.drrr.fluent.cralwer.core.WebDriverPool;
import com.drrr.reader.AbstractCrawlerPageItemReader.CrawlingLocalDatePatterns;
import com.drrr.reader.AbstractCrawlerPageItemReader.CrawlingUtils;
import com.drrr.reader.fluent.ParallelPageItemReader;
import com.drrr.reader.fluent.TechBlogReader;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Slf4j
@Configuration
@RequiredArgsConstructor
public class KakaoCrawler {

    private static final String BASE_URL = "https://tech.kakao.com/blog/";
    private static final String PAGE_URL = "https://tech.kakao.com/blog/page/";
    private static final String PREFIX = "https://tech.kakao.com/";
    private static final TechBlogCode CODE = TechBlogCode.KAKAO;


    @Bean
    TechBlogReader kakaoReader(WebDriverPool webDriverPool) {
        var pages = GenericParallelPages.<ExternalBlogPosts>builder()
                .webDriverPool(webDriverPool)
                .pageInitializer(pageInitializer())
                .contentsLoader(contentsLoader())
                .paginationReader(paginationReader())
                .parallelCount(3)
                .contentsReader(contentsReader())
                .after(data -> log.info("{}", data))
                .build();
        return new ParallelPageItemReader(pages, CODE);
    }

    private ContentsReader<ExternalBlogPosts> contentsReader() {
        return webDriver -> webDriver.findElement(className("elementor-element-150ab43d"))
                .findElements(By.tagName("article"))
                .stream().map(this::parse)
                .collect(collectingAndThen(Collectors.toList(), ExternalBlogPosts::new));
    }

    private ExternalBlogPost parse(WebElement webElement) {
        var titleElement = webElement.findElement(cssSelector("h3 a"));
        var authorElement = webElement.findElement(className("elementor-post-author"));
        var postDateElement = webElement.findElement(className("elementor-post-date"));
        var summaryElement = webElement.findElement(className("elementor-post__excerpt"));

        var link = titleElement.getAttribute("href");
        var dateText = postDateElement.getText()
                .trim()
                .replaceAll("///", "");

        return ExternalBlogPost.builder()
                .code(CODE)
                .link(link)
                .title(titleElement.getText())
                .suffix(link.substring(PREFIX.length()))
                .author(authorElement.getText())
                .postDate(CrawlingLocalDatePatterns.PATTERN1.parse(dateText))
                .summary(summaryElement.getText())
                .build();
    }

    private PaginationReader paginationReader() {
        return webDriver -> PaginationInformation.lastPage(webDriver.findElement(className("elementor-pagination"))
                .findElements(By.tagName("a"))
                .stream()
                .map(WebElement::getText)
                .map(page -> page.replaceAll("Page", ""))
                .filter(CrawlingUtils::isNumber)
                .map(Integer::parseInt)
                .reduce(Integer.MIN_VALUE, Math::max));

    }

    private ParallelPageInitializer pageInitializer() {
        return () -> new BasePageUrls(
                BASE_URL,
                pageNumber -> {
                    log.info("{}", PAGE_URL + pageNumber);
                    return PAGE_URL + pageNumber;
                }
        );
    }

    private ContentsLoader contentsLoader() {
        return new SimpleContentsLoader(className("elementor-posts"));
    }

}
