package com.drrr.parser.blog;

import com.drrr.core.code.techblog.TechBlogCode;
import com.drrr.fluent.cralwer.core.SimpleContentsLoader;
import com.drrr.fluent.cralwer.core.WebDriverPool;
import com.drrr.parser.Parser;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MarketKurlyParser implements Parser {
    private final WebDriverPool webDriverPool;


    @Override
    public String execute(String url) {
        return webDriverPool.delegate(webDriver -> {
            webDriver.get(url);

            var webDriverWait = new WebDriverWait(webDriver, Duration.ofSeconds(10));

            var simpleContentsLoader = new SimpleContentsLoader(By.className("post"));
            simpleContentsLoader.waitUntilLoad(webDriverWait);

            return removeUnnecessaryTag(webDriver.findElement(By.className("post"))
                    .getAttribute("innerText"));
        });
    }

    @Override
    public TechBlogCode getTechBlogCode() {
        return TechBlogCode.MARKET_KURLY;
    }
}
