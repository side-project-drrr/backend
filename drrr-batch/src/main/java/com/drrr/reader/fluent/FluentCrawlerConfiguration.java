package com.drrr.reader.fluent;


import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import com.drrr.core.code.techblog.TechBlogCode;
import com.drrr.fluent.cralwer.core.WebDriverPool;
import com.drrr.property.DriverProperty;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.DestroyMode;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.openqa.selenium.WebDriver;
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
    WebDriverPool webDriverPool(DriverProperty driverProperty) {
        return new WebDriverPool(webDriverPoolFactory(driverProperty), 6);
    }

    @Bean
    BasePooledObjectFactory<WebDriver> webDriverPoolFactory(DriverProperty property) {
        return new FluentWebDriverPoolFactory(property);
    }

    @RequiredArgsConstructor
    static class FluentWebDriverPoolFactory extends BasePooledObjectFactory<WebDriver> {
        private final DriverProperty driverProperty;

        @Override
        public WebDriver create() {
            return driverProperty.createWebDriver();
        }

        @Override
        public PooledObject<WebDriver> wrap(WebDriver driver) {
            return new DefaultPooledObject<>(driver);
        }

        @Override
        public void destroyObject(PooledObject<WebDriver> p, DestroyMode destroyMode) {
            p.getObject().quit();
        }
    }
}
