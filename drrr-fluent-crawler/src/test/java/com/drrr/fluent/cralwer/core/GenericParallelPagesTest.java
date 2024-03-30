package com.drrr.fluent.cralwer.core;

import static org.openqa.selenium.By.className;
import static org.openqa.selenium.By.tagName;

import com.drrr.fluent.cralwer.core.GenericParallelPages.GenericParallelPagesBuilder;
import com.drrr.fluent.cralwer.core.PaginationReader.PaginationInformation;
import com.drrr.fluent.cralwer.core.ParallelPageInitializer.BasePageUrls;
import com.drrr.fluent.cralwer.core.WebDriverPool.WebDriverPoolFactory;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxOptions;


class GenericParallelPagesTest {


    private static final String BASE_URL = "https://blog.dramancompany.com";
    private static final String PAGE_URL = "https://blog.dramancompany.com/page/";


    @Disabled
    @Test
    void 테스트() {
        try (var pool = new WebDriverPool(new WebDriverPoolFactory(new FirefoxOptions()))) {
            var page = new GenericParallelPagesBuilder<String>()
                    .contentsReader((webDriver) -> "")
                    .pageInitializer(() -> new BasePageUrls(
                            BASE_URL,
                            pageNumber -> pageNumber == 1 ? BASE_URL : PAGE_URL + pageNumber
                    ))
                    .contentsLoader(contentsLoader())
                    .paginationReader(paginationReader())
                    .webDriverPool(pool)
                    .build();

            while (!page.execute().isEmpty()) {
            }
        }

    }

    private PaginationReader paginationReader() {
        return webDriver -> PaginationInformation.lastPage(webDriver.findElement(className("jeg_navigation"))
                .findElements(className("page_number"))
                .stream()
                .map(WebElement::getText)
                .filter(this::isNumber)
                .map(Integer::parseInt)
                .reduce(Integer.MIN_VALUE, Math::max));
    }

    boolean isNumber(String n) {
        try {
            Integer.parseInt(n);
            return true;
        } catch (NumberFormatException exception) {
            return false;
        }
    }

    private ContentsLoader contentsLoader() {
        return new SimpleContentsLoader(tagName("body"));
    }

}