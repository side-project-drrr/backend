package com.drrr.reader;

import com.drrr.domain.ExternalBlogPosts;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Supplier;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.batch.item.ItemReader;

@Slf4j
@Getter
public abstract class AbstractCrawlerPageItemReader implements ItemReader<ExternalBlogPosts> {
    protected final WebDriver webDriver;
    protected final WebDriverWait webDriverWait;
    private final CrawlerPageStrategy pageStrategy;
    private int page;
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
        if (CrawlerPageStrategy.SINGLE_PAGE.equals(pageStrategy) && page == 1) {
            return null;
        }
        // 복수의 페이지를 크롤링 하는 경우
        if (CrawlerPageStrategy.PAGE.equals(pageStrategy) && isLastPage()) {
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
        webDriver.get(this.getPageUrlByParameter(page));
        lastPage = this.getLastPage();
    }

    protected int getLastPage() {
        throw new IllegalArgumentException("페이지 전략 연산으로 사용하기 위해서 해당 메서드를 재정의 해야 합니다.");
    }


    protected String getPageUrlByParameter(int page) {
        throw new IllegalArgumentException("페이지 전략 연산으로 사용하기 위해서 해당 메서드를 재정의 해야 합니다.");
    }


    protected void driverWait(By by) {
        this.webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(by));
    }


    @RequiredArgsConstructor
    public enum CrawlingLocalDatePatterns {
        PATTERN1("yyyy.MM.dd"),
        PATTERN2("yyyy.MM.dd."),
        PATTERN3("MMM.d.yyyy", Locale.ENGLISH),
        PATTERN4("yy.MM.dd"),
        PATTERN5("yyyy-MM-dd"),
        PATTERN6("MMM d, yyyy", Locale.ENGLISH),
        PATTERN7("MMMM d, yyyy", Locale.ENGLISH),
        PATTERN8("yyyy년 M월 d일", Locale.KOREAN),
        PATTERN9("yyyy.M.d"),
        PATTERN10(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
        PATTERN11("yyyy.M.d.");


        private final DateTimeFormatter dateTimeFormatter;

        CrawlingLocalDatePatterns(String pattern) {
            this.dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
        }

        CrawlingLocalDatePatterns(String pattern, Locale language) {
            this.dateTimeFormatter = DateTimeFormatter.ofPattern(pattern, language);
        }

        public LocalDate parse(String text) {
            return LocalDate.parse(text, this.dateTimeFormatter);
        }
    }

    public static class CrawlingUtils {
        public static boolean isNumber(String text) {
            try {
                Integer.parseInt(text);
                return true;
            } catch (NumberFormatException exception) {
                return false;
            }
        }

        public static Optional<WebElement> findByElement(Supplier<WebElement> webElementSupplier) {
            try {
                return Optional.ofNullable(webElementSupplier.get());
            } catch (NoSuchElementException noSuchElementException) {
                return Optional.empty();
            }
        }

        public static boolean existsByElement(Runnable runnable) {
            try {
                runnable.run();
                return true;
            } catch (NoSuchElementException noSuchElementException) {
                return false;
            }
        }

        // img 제거
        public static String removeImgBracket(String input) {
            return input.replace("url(", "")
                    .replace(")", "")
                    .replace("\"", "");
        }

        public static String urlDecode(String url) {
            try {
                return URLDecoder.decode(url, StandardCharsets.UTF_8);
            } catch (Exception e) {
                throw new IllegalArgumentException();
            }
        }

        public static String removeParameterWithUrl(String url) {
            return url.substring(0, url.indexOf("?"));

        }

    }

}
