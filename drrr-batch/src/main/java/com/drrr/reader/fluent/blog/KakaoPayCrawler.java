package com.drrr.reader.fluent.blog;


import com.drrr.core.code.techblog.TechBlogCode;
import com.drrr.domain.ExternalBlogPost;
import com.drrr.domain.ExternalBlogPosts;
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
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Slf4j
@Configuration
public class KakaoPayCrawler {

    private static final String BASE_URL = "https://tech.kakaopay.com/";
    private static final String PAGE_URL = "https://tech.kakaopay.com/page/";
    private static final TechBlogCode CODE = TechBlogCode.KAKAO_PAY;


    @Bean
    TechBlogReader kakaoPayReader(WebDriverPool webDriverPool) {
        var pages = GenericParallelPages.<ExternalBlogPosts>builder()
                .pageInitializer(() -> new BasePageUrls(
                        BASE_URL,
                        (pageNumber) -> {
                            log.info("{}", PAGE_URL + pageNumber);
                            return PAGE_URL + pageNumber;
                        }
                ))
                .contentsReader(contentsReader())
                .paginationReader(paginationReader())
                .parallelCount(3)
                .webDriverPool(webDriverPool)
                .contentsLoader(new SimpleContentsLoader(By.className("pagination")))
                .after(data -> log.info("{}", data))
                .build();

        return new ParallelPageItemReader(pages, CODE);
    }

    ContentsReader<ExternalBlogPosts> contentsReader() {
        return webDriver -> webDriver.findElements(By.className("_postListItem_1cl5f_66"))
                .stream()
                .map(this::parse)
                .collect(Collectors.collectingAndThen(Collectors.toList(), ExternalBlogPosts::new));
    }

    private ExternalBlogPost parse(WebElement webElement) {
        var linkElement = webElement.findElement(By.tagName("a"));
        var thumbnailElement = webElement.findElement(By.cssSelector("a picture img"));
        var titleElement = webElement.findElement(By.cssSelector("a strong"));
        var summaryElement = webElement.findElement(By.cssSelector("a p"));
        var postDate = webElement.findElement(By.cssSelector("a time"));

        var link = linkElement.getAttribute("href");
        return ExternalBlogPost.builder()
                .code(CODE)
                .link(link)
                .postDate(CrawlingLocalDatePatterns.PATTERN9.parse(postDate.getText().replace(" ", "")))
                .suffix(link.substring(PAGE_URL.length()))
                .title(titleElement.getText())
                .summary(summaryElement.getText())
                .thumbnailUrl(thumbnailElement.getAttribute("src"))
                .build();
    }

    PaginationReader paginationReader() {
        return webDriver -> PaginationInformation.lastPage(webDriver.findElement(By.className("pagination"))
                .findElements(By.tagName("a"))
                .stream().map(WebElement::getText)
                .map(String::trim)
                .filter(CrawlingUtils::isNumber)
                .map(Integer::parseInt)
                .reduce(Integer.MIN_VALUE, Math::max));
    }


}
