package com.drrr.fluent.cralwer.core;

import com.drrr.fluent.cralwer.core.PaginationReader.PaginationInformation;
import java.util.Objects;
import lombok.Builder;
import org.openqa.selenium.WebDriver;


public class Pages<T> implements Page<T> {

    private final ContentsReader<T> contentsReader;
    private final PaginationReader paginationReader;
    private final WebDriver webDriver;


    private int currentPage = 1;
    private PaginationInformation paginationInformation = null;

    @Builder
    protected Pages(
            ContentsReader<T> contentsReader,
            PaginationReader paginationReader,
            WebDriver webDriver

    ) {
        Objects.requireNonNull(contentsReader);
        Objects.requireNonNull(paginationReader);
        Objects.requireNonNull(webDriver);

        this.contentsReader = contentsReader;
        this.paginationReader = paginationReader;
        this.webDriver = webDriver;
    }

    @Override
    public T execute() {
        if (Objects.nonNull(paginationInformation) && paginationInformation.complete(currentPage)) {
            return null;
        }

        paginationInformation = paginationReader.read(webDriver);

        currentPage++;
        return contentsReader.read(webDriver);
    }

}
