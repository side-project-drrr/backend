package com.drrr.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Configuration;


@Slf4j
@Configuration
@ConditionalOnBean(WebCrawlerBatchConfiguration.class) // Crawling 실행시 빈으로 등록하도록 변경
@RequiredArgsConstructor
public class DriverConfiguration {
}
