package com.drrr.fluent.cralwer.core;


import org.openqa.selenium.WebDriver;

/**
 * 크롤링 하는 최소 단위
 *
 *
 * @formatter:off
 * <p>
 *     *MultiPage
 *     *SinglePage
 *     *ScrollPage
 * </p>
 *
 * @formatterr:on
 */
public interface Page<T> {
    T execute();

    default T cleanup(WebDriver webDriver){
        webDriver.close();
        return null;
    }
}
