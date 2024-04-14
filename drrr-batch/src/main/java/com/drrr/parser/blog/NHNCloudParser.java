package com.drrr.parser.blog;


import com.drrr.core.code.techblog.TechBlogCode;
import com.drrr.fluent.cralwer.core.WebDriverPool;
import com.drrr.parser.Parser;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NHNCloudParser implements Parser {
    private final WebDriverPool webDriverPool;


    @Override
    public String execute(String url) {
        return webDriverPool.delegate(webDriver -> {
            var by = By.className("tui-editor-contents");
            waitUnitlLoad(webDriver, by);
            return removeUnnecessaryTag(webDriver.findElement(by).getAttribute("innerHTML"));
        });
    }

    @Override
    public TechBlogCode getTechBlogCode() {
        return TechBlogCode.NHN_CLOUD;
    }
}
