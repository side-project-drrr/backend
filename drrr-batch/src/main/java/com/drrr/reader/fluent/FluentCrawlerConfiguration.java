package com.drrr.reader.fluent;


import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import com.drrr.core.code.techblog.TechBlogCode;
import com.drrr.fluent.cralwer.core.WebDriverPool;
import com.drrr.fluent.cralwer.core.WebDriverPool.WebDriverPoolFactory;
import java.util.List;
import java.util.Map;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FluentCrawlerConfiguration {


    @Bean
    Map<TechBlogCode, TechBlogReader> fluentPageItemReaders(List<TechBlogReader> pageItemReaders) {
        return pageItemReaders.stream()
                .collect(toMap(
                        TechBlogReader::getTechBlogCode,
                        identity()
                ));

    }

    @Bean(destroyMethod = "close")
    WebDriverPool webDriverPool() {
        var webDriverPoolFactory = new WebDriverPoolFactory(new FirefoxOptions());
        return new WebDriverPool(webDriverPoolFactory);
    }
}
