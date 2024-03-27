package com.drrr.fluent.cralwer.core;

import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * 크롤링하는 페이지의 컨텐츠가 로딩 될 때 까지 대기 합니다.
 */
public interface ContentsLoader {
    void waitUntilLoad(WebDriverWait webDriverWait);
}
