package com.drrr.fluent.cralwer.core;

import org.openqa.selenium.WebDriver;

/**
 * 페이지의 일부 영역 혹은 특정 부분의 내용을 추출하는 인터페이스 입니다.
 *
 * @param <T>
 */
public interface ContentsReader<T> {
    T read(WebDriver webDriver);
}
