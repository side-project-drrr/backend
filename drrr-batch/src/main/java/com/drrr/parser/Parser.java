package com.drrr.parser;

import com.drrr.core.code.techblog.TechBlogCode;
import com.drrr.fluent.cralwer.core.SimpleContentsLoader;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;


public interface Parser {
    String execute(final String url);


    TechBlogCode getTechBlogCode();


    default Document getDocument(String url) {
        url = URLDecoder.decode(url, StandardCharsets.UTF_8);

        try {
            return Jsoup.connect(String.valueOf(new URL(url).toExternalForm())).get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    default void waitUnitlLoad(WebDriver webDriver, By by) {
        new SimpleContentsLoader(by).waitUntilLoad(new WebDriverWait(webDriver, Duration.ofSeconds(10)));
    }


    default String removeUnnecessaryTag(String html) {
        var document = Jsoup.parse(html);

        document.select("a").remove();

        document.select("code").remove();
        document.select("br").append("\\n");
        document.select("p").prepend("\\n\\n");

        return document.html();
    }
}
