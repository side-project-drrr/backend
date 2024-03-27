package com.drrr.fluent.cralwer.core;

import com.drrr.fluent.cralwer.core.PaginationReader.PaginationInformation;
import com.drrr.fluent.cralwer.core.ParallelPageInitializer.BasePageUrls;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

class ParallelPagesTest {


    @Disabled("성능 테스트")
    @Test()
    void a() {

        // 6페이지 정도
        // 33초
        // 56초 시간 차이 더 벌어질 듯?
        ParallelPages.builder()
                .contentsReader(webDriver -> "")
                .pageInitializer(() -> new BasePageUrls(
                                "https://squarelab.co/blog/?page=1",
                                (page) -> "https://squarelab.co/blog/?page=" + page
                        )

                )
                .webDriver(new FirefoxDriver())
                .paginationReader(webDriver -> PaginationInformation.lastPage(webDriver.findElement(By.id("pagination"))
                        .findElements(By.tagName("a"))
                        .stream()
                        .map(WebElement::getText)
                        .map(Integer::parseInt)
                        .reduce(0, Math::max))
                )
                .contentsLoader(webDriverWait -> new SimpleContentsLoader(By.className("blog-row")))
                .build()
                .execute();
    }

}