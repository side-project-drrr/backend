package com.example.drrrbatch.baemin.service;

import org.openqa.selenium.WebDriver;

import java.util.List;
import java.util.function.Function;

public interface SeleniumCrawlerTemplate<T, K> {

    void openWebDriver();

    List<T> execute(Function<K,T> function);

    void closeWebDriver(WebDriver driver);
}
