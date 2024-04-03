package com.drrr.reader.fluent.blog;


import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

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
import com.drrr.reader.AbstractCrawlerPageItemReader.CrawlingUtils;
import com.drrr.reader.fluent.ParallelPageItemReader;
import com.drrr.reader.fluent.TechBlogReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Slf4j
@Configuration
public class BespinGlobalCrawler {
    private static final String TARGET_URL = "https://blog.bespinglobal.com/";
    private static final String PREFIX_URK = "https://blog.bespinglobal.com/page/";
    private static final TechBlogCode CODE = TechBlogCode.BESPIN_GLOBAL;

    @Bean
    TechBlogReader bespinGlobalTechBlogReader(WebDriverPool webDriverPool) {
        var page = GenericParallelPages.<ExternalBlogPosts>builder()
                .pageInitializer(
                        () -> new BasePageUrls(TARGET_URL, (number) -> number == 1 ? TARGET_URL : PREFIX_URK + number))
                .webDriverPool(webDriverPool)
                .contentsLoader(new SimpleContentsLoader(By.className("nav-links")))
                .parallelCount(3)
                .contentsReader(contentsReader())
                .paginationReader(paginationReader())
                .after(data -> log.info("{}", data))
                .build();
        return new ParallelPageItemReader(page, CODE);
    }

    private ContentsReader<ExternalBlogPosts> contentsReader() {
        return webDriver -> webDriver.findElements(By.tagName("article"))
                .stream()
                .map(this::parse)
                .collect(collectingAndThen(toList(), ExternalBlogPosts::new));
    }

    private ExternalBlogPost parse(WebElement article) {
        var titleElement = article.findElement(By.className("entry-title"));
        var aTag = titleElement.findElement(By.tagName("a"));
        var title = aTag.getText();
        var href = CrawlingUtils.urlDecode(aTag.getAttribute("href"));
        var datetime = article.findElement(By.className("entry-date")).getAttribute("datetime");
        var date = LocalDateTime.parse(datetime, DateTimeFormatter.ISO_OFFSET_DATE_TIME).toLocalDate();

        var summary = CrawlingUtils.findByElement(() -> article.findElement(By.className("entry-summary")));
        var thumbnail = CrawlingUtils.findByElement(() -> article.findElement(By.tagName("img")));

        return ExternalBlogPost.builder()
                .suffix(href.substring(PREFIX_URK.length()))
                .link(href)
                .title(title)
                .postDate(date)
                .thumbnailUrl(thumbnail.map(webElement -> webElement.getAttribute("src")).orElse(null))
                .summary(summary.map(WebElement::getText).orElse(null))
                .code(CODE)
                .build();
    }

    private PaginationReader paginationReader() {
        return webDriver -> PaginationInformation.lastPage(webDriver.findElement(By.className("nav-links"))
                .findElements(By.className("page-numbers"))
                .stream()
                .map(WebElement::getText)
                .filter(pageText -> pageText.startsWith("Page"))
                .map(text -> text.substring("Page".length()))
                .map(String::trim)
                .peek(t -> log.info("check {}", t))
                .filter(CrawlingUtils::isNumber)
                .mapToInt(Integer::parseInt)
                .reduce(Integer.MIN_VALUE, Math::max));
    }
}

