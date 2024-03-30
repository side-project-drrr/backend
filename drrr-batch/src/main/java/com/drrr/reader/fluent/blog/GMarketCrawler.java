package com.drrr.reader.fluent.blog;


import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;
import static org.openqa.selenium.By.className;
import static org.openqa.selenium.By.cssSelector;
import static org.openqa.selenium.By.tagName;

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
import com.drrr.reader.AbstractCrawlerPageItemReader.CrawlingUtils;
import com.drrr.reader.fluent.ParallelPageItemReader;
import com.drrr.reader.fluent.TechBlogReader;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Slf4j
@Configuration
public class GMarketCrawler {

    private static final TechBlogCode CODE = TechBlogCode.BASE;
    private static final String BASE_URL = "https://dev.gmarket.com/";
    private static final String PAGE_URL = "https://dev.gmarket.com/?page=";


    @Bean
    TechBlogReader gMargetReader(WebDriverPool webDriverPool) {
        var page = GenericParallelPages.<ExternalBlogPosts>builder()
                .webDriverPool(webDriverPool)
                .parallelCount(3)
                .pageInitializer(() -> new BasePageUrls(BASE_URL, (number) -> PAGE_URL + number))
                .paginationReader(paginationReader())
                .contentsReader(contentsReader())
                .contentsLoader(contentsLoader())
                .after(data -> log.info("{}", data))
                .build();

        return new ParallelPageItemReader(page, CODE);
    }

    private ContentsLoader contentsLoader() {
        return new SimpleContentsLoader(className("inner_paging"));
    }

    private ContentsReader<ExternalBlogPosts> contentsReader() {
        return document -> document.findElement(By.id("cMain"))
                .findElements(By.id("mArticle"))
                .stream().map(this::parse)
                .collect(collectingAndThen(toList(), ExternalBlogPosts::new));
    }

    private ExternalBlogPost parse(WebElement document) {
        var linkElement = document.findElement(cssSelector(".link_post"));
        var titleElement = linkElement.findElement(tagName("strong"));
        var summaryElement = linkElement.findElement(tagName("p"));

        var link = linkElement.getAttribute("href");
        var metaData = document.findElement(className("detail_info")).getText();

        var split = metaData.trim().split("  ");

        var namePart = split[0].split(" ");
        var timePart = split[2].split(" ");
        var author = namePart[namePart.length - 1];
        var postDate = CrawlingLocalDatePatterns.PATTERN11.parse(timePart[0] + timePart[1] + timePart[2]);

        return ExternalBlogPost.builder()
                .title(titleElement.getText())
                .summary(summaryElement.getText())
                .postDate(postDate)
                .author(author)
                .suffix(link.substring(BASE_URL.length()))
                .link(link)
                .code(CODE)
                .build();
    }

    private PaginationReader paginationReader() {
        return webDriver -> PaginationInformation.lastPage(webDriver.findElement(className("inner_paging"))
                .findElements(tagName("a"))
                .stream()
                .map(WebElement::getText)
                .filter(CrawlingUtils::isNumber)
                .map(Integer::parseInt)
                .reduce(Integer.MIN_VALUE, Math::max));
    }

}
