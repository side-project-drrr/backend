package com.drrr.fluent.cralwer.core;

import com.drrr.fluent.cralwer.core.PaginationReader.PaginationInformation;
import com.drrr.fluent.cralwer.core.SinglePage.Mode;
import com.drrr.fluent.cralwer.core.SinglePage.SinglePageBuilder;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;
import lombok.Builder;
import org.openqa.selenium.support.ui.WebDriverWait;


public class GenericParallelPages<T> implements MultiPage<T> {
    private static final int DEFAULT_PARALLEL_COUNT = 2;
    private final WebDriverPool webDriverPool;
    private final ParallelPageInitializer pageInitializer;
    private final ContentsReader<T> contentsReader;
    private final PaginationReader paginationReader;
    private final ContentsLoader contentsLoader;
    private final After<T> after;
    private final int parallelCount;


    private int currentPage = 1;
    private PaginationInformation paginationInformation;

    @Builder
    public GenericParallelPages(
            WebDriverPool webDriverPool,
            ParallelPageInitializer pageInitializer,
            ContentsReader<T> contentsReader,
            PaginationReader paginationReader,
            ContentsLoader contentsLoader,
            After<T> after,
            int parallelCount
    ) {
        this.webDriverPool = webDriverPool;
        this.pageInitializer = pageInitializer;
        this.contentsReader = contentsReader;
        this.paginationReader = paginationReader;
        this.contentsLoader = contentsLoader;
        this.parallelCount = parallelCount <= 0 ? DEFAULT_PARALLEL_COUNT : parallelCount;
        this.after = after;
    }

    @Override
    public List<T> execute() {

        var initializeUrl = pageInitializer.getUrl();

        if (Objects.nonNull(paginationInformation) && paginationInformation.complete(currentPage)) {
            return Collections.emptyList();
        }

        preLoaded(initializeUrl.home());

        var runnerCount = Math.min(
                paginationInformation.remainPage(currentPage),
                parallelCount
        );
        var urlGenerator = initializeUrl.searchUrlGenerator();

        var results = IntStream.iterate(currentPage, i -> i + 1)
                .parallel()
                .limit(runnerCount)
                .mapToObj(urlGenerator::apply)
                .map((url) -> createSinglePage(url).execute())
                .toList();

        currentPage += runnerCount;
        return results;
    }

    private void after(T data) {
        if (Objects.nonNull(after)) {
            after.action(data);
        }
    }

    // 메인 크롤러에서 컨텐츠 로드, 페이지네이션 로드
    private void preLoaded(String home) {
        if (Objects.nonNull(paginationInformation)) {
            return;
        }
        //webDriverPool.preLoadDriver(parallelCount);
        this.paginationInformation = webDriverPool.delegate(webDriver -> {
            webDriver.get(home);
            contentsLoader.waitUntilLoad(new WebDriverWait(
                    webDriver,
                    Duration.ofSeconds(3))
            );

            var paginationInformation = paginationReader.read(webDriver);
            if (!paginationInformation.hasLastPage()) {
                throw new IllegalStateException("last page가 없는 경우 해당 기능을 지원하지 않습니다.");
            }
            return paginationInformation;
        });
    }


    private Page<T> createSinglePage(String url) {
        return new SinglePageBuilder<T>()
                .mode(Mode.POLLING)
                .singlePageInitializer(() -> url)
                .contentsReader(contentsReader)
                .contentsLoader(contentsLoader)
                .webDriverCleaner(webDriverPool::returnObject)
                .webDriver(webDriverPool.borrow())
                .after(this::after)
                .build();
    }
}
