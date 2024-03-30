package com.drrr.fluent.cralwer.core;


import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * 특정 상황을 만족하는 경우 크롤링을 중단하는 인터페이스입니다.
 */
public interface PagesStopper {
    boolean isSatisfy(WebDriver webDriver, WebDriverWait webDriverWait);
}
