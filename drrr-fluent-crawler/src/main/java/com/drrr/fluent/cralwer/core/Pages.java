package com.drrr.fluent.cralwer.core;

import com.drrr.fluent.cralwer.core.PaginationReader.PaginationInformation;
import java.time.Duration;
import java.util.Objects;
import java.util.Optional;
import lombok.Builder;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;


public class Pages<T> implements Page<T> {

    private final PagesInitializer pagesInitializer;
    private final ContentsLoader contentsLoader;
    private final ContentsReader<T> contentsReader;
    private final PaginationReader paginationReader;
    private final WebDriver webDriver;
    private final WebDriverWait webDriverWait;


    private int currentPage = 1;
    private PaginationInformation paginationInformation = null;

    @Builder
    protected Pages(
            PagesInitializer pagesInitializer,
            ContentsReader<T> contentsReader,
            PaginationReader paginationReader,
            ContentsLoader contentsLoader,
            WebDriver webDriver

    ) {
        Objects.requireNonNull(pagesInitializer);
        Objects.requireNonNull(contentsReader);
        Objects.requireNonNull(paginationReader);
        Objects.requireNonNull(webDriver);
        Objects.requireNonNull(contentsLoader);

        this.pagesInitializer = pagesInitializer;
        this.contentsReader = contentsReader;
        this.paginationReader = paginationReader;
        this.contentsLoader = contentsLoader;
        this.webDriver = webDriver;
        this.webDriverWait = new WebDriverWait(webDriver, Duration.ofSeconds(10));
    }

    @Override
    public T execute() {
        return Optional.ofNullable(subExecute())
                .orElseGet(() -> cleanup(webDriver));
    }

    private T subExecute() {
        if (Objects.nonNull(paginationInformation) && paginationInformation.complete(currentPage)) {
            return null;
        }
        webDriver.get(pagesInitializer.getUrl(currentPage));

        contentsLoader.waitUntilLoad(webDriverWait);

        paginationInformation = paginationReader.read(webDriver);

        currentPage++;

        return contentsReader.read(webDriver);
    }

}
