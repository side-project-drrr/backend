package com.example.drrrbatch.batch.reader;

import static com.example.drrrbatch.batch.reader.CrawlerPageStrategy.PAGE;
import static com.example.drrrbatch.batch.reader.CrawlerPageStrategy.SINGLE_PAGE;

import com.example.drrrbatch.batch.domain.ExternalBlogPosts;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.batch.item.ItemReader;

@Slf4j

public abstract class AbstractCrawlerPageItemReader implements ItemReader<ExternalBlogPosts> {

    /**
     * <p> ex)2022.02.02 </p>
     */
    protected static final DateTimeFormatter FORMATTER1 = DateTimeFormatter.ofPattern("yyyy.MM.dd");
    /**
     * <p>ex)2022.02.02.</p>
     */
    protected static final DateTimeFormatter FORMATTER2 = DateTimeFormatter.ofPattern("yyyy.MM.dd.");
    protected final WebDriver webDriver;
    protected final WebDriverWait webDriverWait;
    private final CrawlerPageStrategy pageStrategy;
    private int page = -1;
    private int lastPage = Integer.MIN_VALUE;


    protected AbstractCrawlerPageItemReader(CrawlerPageStrategy crawlerPageStrategy, WebDriver webDriver) {
        this.pageStrategy = crawlerPageStrategy;
        this.webDriverWait = new WebDriverWait(webDriver, Duration.ofSeconds(10));
        this.webDriver = webDriver;
        this.page = 0;
    }

    @Override
    public ExternalBlogPosts read() {
        // 단일 페이지의 경우 1번만 실행 되도록 설정
        if (SINGLE_PAGE.equals(pageStrategy) && page == 1) {
            return null;
        }
        // 복수의 페이지를 크롤링 하는 경우
        if (PAGE.equals(pageStrategy) && isLastPage()) {
            return null;
        }
        page++;
        return this.executeCrawlerPage();
    }

    private boolean isLastPage() {
        log.info("page hit {} {}", page, lastPage);
        return page == lastPage + 1;
    }

    protected abstract ExternalBlogPosts executeCrawlerPage();

    protected void selectPage() {
        this.webDriver.get(this.getPageUrlByParameter(page));
        waitPage();
        this.lastPage = this.getLastPage();
    }

    protected int getLastPage() {
        throw new IllegalArgumentException("페이지 전략 연산으로 사용하기 위해서 해당 메서드를 재정의 해야 합니다.");
    }

    protected String getPageUrlByParameter(int page) {
        throw new IllegalArgumentException("페이지 전략 연산으로 사용하기 위해서 해당 메서드를 재정의 해야 합니다.");
    }

    private void waitPage() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
