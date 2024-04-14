package com.drrr.parser.blog;


import com.drrr.core.code.techblog.TechBlogCode;
import com.drrr.fluent.cralwer.core.WebDriverPool;
import com.drrr.parser.Parser;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DevOceanParser implements Parser {
    private final WebDriverPool webDriverPool;

    @Override
    public String execute(String url) {
        return webDriverPool.delegate(webDriver -> {

            webDriver.get(url);
            var by = By.className("toastui-editor-contents");
            waitUnitlLoad(webDriver, by);

            return removeUnnecessaryTag(webDriver.findElement(by).getAttribute("innerHTML"));
        });
    }

    @Override
    public TechBlogCode getTechBlogCode() {
        return TechBlogCode.DEVOCEAN;
    }
}
