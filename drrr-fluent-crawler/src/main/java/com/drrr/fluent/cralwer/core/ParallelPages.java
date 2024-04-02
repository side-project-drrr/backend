package com.drrr.fluent.cralwer.core;

import com.drrr.fluent.cralwer.core.PaginationReader.PaginationInformation;
import com.drrr.fluent.cralwer.core.SinglePage.Mode;
import com.drrr.fluent.cralwer.core.SinglePage.SinglePageBuilder;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;
import lombok.Builder;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.WebDriverWait;


/**
 * 크롤링 페이지 단위로 병렬 처리
 *
 * @param <T>
 */
public class ParallelPages<T> implements MultiPage<T> {
    private static final int PARALLEL_COUNT = 10;
    private final ParallelPageInitializer pageInitializer;
    private final ContentsReader<T> contentsReader;
    private final ContentsLoader contentsLoader;
    private final PaginationReader paginationReader;
    private final WebDriver webDriver;
    private final WebDriverWait webDriverWait;


    private int currentPage = 1;
    private PaginationInformation paginationInformation;


    @Builder
    public ParallelPages(
            ParallelPageInitializer pageInitializer,
            ContentsReader<T> contentsReader,
            ContentsLoader contentsLoader,
            PaginationReader paginationReader,
            WebDriver webDriver
    ) {
        this.pageInitializer = pageInitializer;
        this.contentsReader = contentsReader;
        this.contentsLoader = contentsLoader;
        this.paginationReader = paginationReader;
        this.webDriver = webDriver;
        this.webDriverWait = new WebDriverWait(webDriver, Duration.ofSeconds(10));
    }

    @Override
    public List<T> execute() {
        var initializerUrl = pageInitializer.getUrl();

        if (Objects.nonNull(paginationInformation) && paginationInformation.complete(currentPage)) {
            CompletableFuture.runAsync(webDriver::close);
            return Collections.emptyList();
        }

        preLoaded(initializerUrl.home());
        var runnerCount = Math.min(
                paginationInformation.remainPage(currentPage),
                PARALLEL_COUNT
        );

        var urlGenerator = initializerUrl.searchUrlGenerator();
        var executors = Executors.newFixedThreadPool(runnerCount);
        var completableFutures = IntStream.iterate(currentPage, i -> i + 1)
                .limit(runnerCount)
                .mapToObj(urlGenerator::apply)
                .map(url -> CompletableFuture.supplyAsync(() -> createSinglePage(url), executors))
                .map(createPageAction -> createPageAction.thenApply(Page::execute))
                .toList();

        var results = completableFutures.stream()
                .map(CompletableFuture::join)
                .filter(Objects::nonNull)
                .toList();
        currentPage += runnerCount;
        executors.shutdown();

        return results;


    }

    // 메인 크롤러에서 컨텐츠 로드, 페이지네이션 로드
    private void preLoaded(String home) {
        if (Objects.isNull(paginationInformation)) {
            webDriver.get(home);
            contentsLoader.waitUntilLoad(webDriverWait);

            var paginationInformation = paginationReader.read(webDriver);
            if (!paginationInformation.hasLastPage()) {
                throw new IllegalStateException("last page가 없는 경우 해당 기능을 지원하지 않습니다.");
            }
            this.paginationInformation = paginationInformation;
        }
    }

    private Page<T> createSinglePage(String url) {
        return new SinglePageBuilder<T>()
                .mode(Mode.NON_LIMIT)
                .singlePageInitializer(() -> url)
                .contentsReader(contentsReader)
                .contentsLoader(contentsLoader)
                .webDriver(new FirefoxDriver(new FirefoxOptions().addArguments("--headless")))
                .build();
    }

}
