package com.drrr.parser;


import com.drrr.core.code.techblog.TechBlogCode;
import com.drrr.fluent.cralwer.core.WebDriverPool;
import java.util.Objects;
import lombok.Builder;
import org.openqa.selenium.By;

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
