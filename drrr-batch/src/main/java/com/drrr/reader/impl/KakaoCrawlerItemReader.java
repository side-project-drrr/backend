package com.drrr.reader.impl;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

import com.drrr.core.code.techblog.TechBlogCode;
import com.drrr.domain.ExternalBlogPost;
import com.drrr.domain.ExternalBlogPosts;
import com.drrr.reader.AbstractCrawlerPageItemReader;
import com.drrr.reader.CrawlerPageStrategy;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * page url format https://tech.kakao.com/blog/page/2/#posts {url}/blog/page/{}/#posts
 */
@Slf4j
public class KakaoCrawlerItemReader extends AbstractCrawlerPageItemReader {
    private static final TechBlogCode TECH_BLOG_CODE = TechBlogCode.KAKAO;
    private static final String PAGE_URL = "https://tech.kakao.com";
    private static final int PREFIX_LENGTH = PAGE_URL.length();
    private static final String PAGE_FORMAT = PAGE_URL + "/blog/page/%d/#posts";
    private static final String POST_CLASS_NAME = "elementor-posts--skin-classic";


    public KakaoCrawlerItemReader(WebDriver webDriver) {
        super(CrawlerPageStrategy.PAGE, webDriver);
    }

    @Override
    protected ExternalBlogPosts executeCrawlerPage() {
        this.selectPage();
        this.webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.className(POST_CLASS_NAME)));
        return this.webDriver.findElement(By.className(POST_CLASS_NAME))
                .findElements(By.tagName("article"))
                .stream().map(webElement -> {
                    final WebElement postElement = webElement.findElement(By.className("elementor-post__text"));
                    final WebElement postTitleElement = postElement.findElement(By.className("elementor-post__title"))
                            .findElement(By.tagName("a"));
                    final WebElement postMetaData = postElement.findElement(By.className("elementor-post__meta-data"));
                    final WebElement postAuthor = postMetaData.findElement(By.className("elementor-post-author"));
                    final WebElement postDate = postMetaData.findElement(By.className("elementor-post-date"));
                    final WebElement postSummary = postElement.findElement(By.className("elementor-post__excerpt"))
                            .findElement(By.tagName("p"));

                    return ExternalBlogPost.builder()
                            .code(TECH_BLOG_CODE)
                            .title(postTitleElement.getText())
                            .suffix(postTitleElement.getAttribute("href").substring(PREFIX_LENGTH))
                            .link(postTitleElement.getAttribute("href"))
                            .author(postAuthor.getText())
                            .summary(postSummary.getText())
                            .postDate(CrawlingLocalDatePatterns.PATTERN1.parse(postDate.getText()))
                            .build();
                }).collect(collectingAndThen(toList(), ExternalBlogPosts::new));

    }


    @Override
    protected int getLastPage() {
        this.webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.className("page-numbers")));
        return this.webDriver.findElement(By.className("elementor-pagination"))
                .findElements(By.tagName("a"))
                .stream()
                .map(WebElement::getText)
                .filter(this::isNumber)
                .mapToInt(Integer::parseInt)
                .max()
                .orElseThrow();
    }

    @Override
    protected String getPageUrlByParameter(int page) {
        final String url = String.format(PAGE_FORMAT, page);
        log.info("crawler kakao url: {}", url);
        return url;
    }

    public boolean isNumber(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (Exception exception) {
            return false;
        }
    }
}
