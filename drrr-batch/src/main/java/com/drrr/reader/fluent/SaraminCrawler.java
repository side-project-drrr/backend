package com.drrr.reader.fluent;


import com.drrr.core.code.techblog.TechBlogCode;
import com.drrr.domain.ExternalBlogPosts;
import com.drrr.fluent.cralwer.core.ContentsLoader;
import com.drrr.fluent.cralwer.core.ContentsReader;
import com.drrr.fluent.cralwer.core.Pages;
import com.drrr.fluent.cralwer.core.PagesInitializer;
import com.drrr.fluent.cralwer.core.PaginationReader;
import com.drrr.fluent.cralwer.core.PaginationReader.PaginationInformation;
import com.drrr.fluent.cralwer.core.SimpleContentsLoader;
import com.drrr.reader.AbstractCrawlerPageItemReader.CrawlingUtils;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SaraminCrawler {

    private static final String BASE_URL = "https://saramin.github.io/";
    private static final String PAGE_URL = "https://saramin.github.io/page";

    @Bean
    public PageItemReader saraminPages(WebDriver webDriver) {
        var page = Pages.<ExternalBlogPosts>builder()
                .pagesInitializer(pagesInitializer())
                .contentsLoader(contentsLoader())
                .paginationReader(paginationReader())
                .contentsReader(contentsReader())
                .webDriver(webDriver)
                .build();
        return new PageItemReader(page, TechBlogCode.SARAMIN);

    }

    PagesInitializer pagesInitializer() {
        return pageNumber -> {
            if (pageNumber == 1) {
                return BASE_URL;
            }
            return PAGE_URL + pageNumber;
        };
    }

    ContentsLoader contentsLoader() {
        return new SimpleContentsLoader(By.className("container"));
    }

    ContentsReader<ExternalBlogPosts> contentsReader() {
        return driver -> new ExternalBlogPosts(List.of());
    }

    PaginationReader paginationReader() {
        return driver -> {
            var pager = driver.findElement(By.className("pager"));

            if (!CrawlingUtils.existsByElement(() -> pager.findElement(By.className("next")))) {
                return PaginationInformation.stopInformation();
            }

            var maxPage = pager.findElements(By.className("next"))
                    .stream()
                    .map(next -> next.findElement(By.tagName("a"))
                            .getAttribute("href")
                            .substring(PAGE_URL.length()))
                    .mapToInt(Integer::parseInt)
                    .max()
                    .orElseThrow();

            return new PaginationInformation(maxPage);
        };
    }
}
