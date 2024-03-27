package com.drrr.fluent.cralwer.core;


/**
 * 크롤링 하는 페이지의 url 가져오는 인터페이스입니다.
 */
public interface PagesInitializer {
    String getUrl(int pageNumber);
}
