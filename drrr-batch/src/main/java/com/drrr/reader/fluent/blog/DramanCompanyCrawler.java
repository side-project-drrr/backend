package com.drrr.reader.fluent.blog;


import static com.drrr.reader.AbstractCrawlerPageItemReader.CrawlingUtils.findByElement;
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
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
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
    TechBlogReader DramanCompanyPageItemReader(WebDriverPool webDriverPool) {
        var pages = GenericParallelPages.<ExternalBlogPosts>builder()
                .parallelCount(3)
                .webDriverPool(webDriverPool)
                .pageInitializer(() -> new BasePageUrls(
                        BASE_URL,
                        pageNumber -> pageNumber == 1 ? BASE_URL : PAGE_URL + pageNumber
                ))
                .paginationReader(paginationReader())
                .contentsLoader(contentsLoader())
                .contentsReader(contentsReader())
                .after(data -> log.info("{}", data))
                .build();
        return new ParallelPageItemReader(pages, CODE);
    }

    private PaginationReader paginationReader() {
        return webDriver -> PaginationInformation.lastPage(webDriver.findElement(className("jeg_navigation"))
                .findElements(className("page_number"))
                .stream()
                .map(WebElement::getText)
                .filter(CrawlingUtils::isNumber)
                .map(Integer::parseInt)
                .reduce(Integer.MIN_VALUE, Math::max));
    }

    private ContentsLoader contentsLoader() {
        return new SimpleContentsLoader(tagName("body"));
    }

    private ContentsReader<ExternalBlogPosts> contentsReader() {
        return webDriver -> webDriver.findElement(className("jeg_posts"))
                .findElements(className("jeg_post"))
                .stream().map(this::parse)
                .peek(externalBlogPost -> log.info("check {}", externalBlogPost))
                .collect(Collectors.collectingAndThen(Collectors.toList(), ExternalBlogPosts::new));
    }

    private ExternalBlogPost parse(WebElement webElement) {
        var thumbnailElement = findByElement(() -> webElement.findElement(cssSelector(".jeg_thumb a img")));
        var titleElement = webElement.findElement(cssSelector(".jeg_postblock_content .jeg_post_title a"));
        var dateTimeElement = webElement.findElement(cssSelector(".jeg_post_meta .jeg_meta_date"));
        var summaryElement = webElement.findElement(cssSelector(".jeg_post_excerpt p"));

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
