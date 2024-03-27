package com.drrr.reader.fluent.blog;


import com.drrr.core.code.techblog.TechBlogCode;
import com.drrr.domain.ExternalBlogPost;
import com.drrr.domain.ExternalBlogPosts;
import com.drrr.fluent.cralwer.core.ContentsLoader;
import com.drrr.fluent.cralwer.core.ContentsReader;
import com.drrr.fluent.cralwer.core.PaginationReader;
import com.drrr.fluent.cralwer.core.PaginationReader.PaginationInformation;
import com.drrr.fluent.cralwer.core.ParallelPageInitializer.BasePageUrls;
import com.drrr.fluent.cralwer.core.ParallelPages;
import com.drrr.fluent.cralwer.core.SimpleContentsLoader;
import com.drrr.reader.AbstractCrawlerPageItemReader.CrawlingLocalDatePatterns;
import com.drrr.reader.AbstractCrawlerPageItemReader.CrawlingUtils;
import com.drrr.reader.fluent.ParallelPageItemReader;
import com.drrr.reader.fluent.TechBlogReader;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Slf4j
@Configuration
public class DramanCompanyCrawler {

    private static final String BASE_URL = "https://blog.dramancompany.com";
    private static final String PAGE_URL = "https://blog.dramancompany.com/page/";
    private static final TechBlogCode CODE = TechBlogCode.DRAMANCOMPANY;


    @Bean
    TechBlogReader DramanCompanyPageItemReader(WebDriver webDriver) {
        var pages = ParallelPages.<ExternalBlogPosts>builder()
                .contentsReader(contentsReader())
                .pageInitializer(() -> new BasePageUrls(
                        BASE_URL,
                        pageNumber -> pageNumber == 1 ? BASE_URL : PAGE_URL + pageNumber
                ))
                .webDriver(webDriver)
                .contentsLoader(contentsLoader())
                .paginationReader(paginationReader())
                .build();

        return new ParallelPageItemReader(pages, CODE);
    }

    private PaginationReader paginationReader() {
        return webDriver -> PaginationInformation.lastPage(webDriver.findElement(By.className("jeg_navigation"))
                .findElements(By.className("page_number"))
                .stream()
                .map(WebElement::getText)
                .filter(CrawlingUtils::isNumber)
                .map(Integer::parseInt)
                .reduce(Integer.MIN_VALUE, Math::max));
    }

    private ContentsLoader contentsLoader() {
        return new SimpleContentsLoader(By.tagName("body"));
    }

    private ContentsReader<ExternalBlogPosts> contentsReader() {
        return webDriver -> webDriver.findElement(By.className("jeg_posts"))
                .findElements(By.className("jeg_post"))
                .stream().map(this::parse)
                .peek(externalBlogPost -> log.info("check {}", externalBlogPost))
                .collect(Collectors.collectingAndThen(Collectors.toList(), ExternalBlogPosts::new));
    }

    private ExternalBlogPost parse(WebElement webElement) {
        var thumbnailElement = CrawlingUtils.findByElement(
                () -> webElement.findElement(By.cssSelector(".jeg_thumb a img")));
        var titleElement = webElement.findElement(By.cssSelector(".jeg_postblock_content .jeg_post_title a"));
        var dateTimeElement = webElement.findElement(By.cssSelector(".jeg_post_meta .jeg_meta_date"));
        var summaryElement = webElement.findElement(By.cssSelector(".jeg_post_excerpt p"));

        var link = titleElement.getAttribute("href");

        var url = CrawlingUtils.urlDecode(link);

        return ExternalBlogPost.builder()
                .thumbnailUrl(thumbnailElement.map((element) -> element.getAttribute("src")).orElse(null))
                .summary(summaryElement.getText())
                .title(titleElement.getText())
                .suffix(url.substring(BASE_URL.length()))
                .postDate(CrawlingLocalDatePatterns.PATTERN8.parse(dateTimeElement.getText()))
                .link(url)
                .code(CODE)
                .build();

    }
}
