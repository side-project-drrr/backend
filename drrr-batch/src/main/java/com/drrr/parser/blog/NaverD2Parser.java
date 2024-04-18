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
public class NaverD2Parser implements Parser {
    private final WebDriverPool webDriverPool;

    @Override
    public String execute(String url) {

        return webDriverPool.delegate(webDriver -> {
            var by = By.className("con_view");

            webDriver.get(url);
            new SimpleContentsLoader(by)
                    .waitUntilLoad(new WebDriverWait(webDriver, Duration.ofSeconds(10)));

            return removeUnnecessaryTag(webDriver.findElement(by).getAttribute("innerHTML"));
        });
    }

    @Override
    public TechBlogCode getTechBlogCode() {
        return TechBlogCode.NAVER;
    }
}
