package com.drrr.parser;


import com.drrr.core.code.techblog.TechBlogCode;
import com.drrr.fluent.cralwer.core.WebDriverPool;
import java.util.Objects;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;


@Slf4j
@Builder
public class SimpleParser implements Parser {
    private final WebDriverPool webDriverPool;
    private final By target;
    private final TechBlogCode techBlogCode;


    public SimpleParser(WebDriverPool webDriverPool, By target, TechBlogCode techBlogCode) {
        Objects.requireNonNull(webDriverPool);
        Objects.requireNonNull(target);
        Objects.requireNonNull(techBlogCode);

        this.webDriverPool = webDriverPool;
        this.target = target;
        this.techBlogCode = techBlogCode;
    }

    @Override
    public String execute(String url) {
        log.info("{}", url);
        return webDriverPool.delegate(webDriver -> {
            webDriver.get(url);
            waitUnitlLoad(webDriver, target);
            return removeUnnecessaryTag(webDriver.findElement(target).getAttribute("innerHTML"));
        });
    }

    @Override
    public TechBlogCode getTechBlogCode() {
        return techBlogCode;
    }
}
