package com.drrr.study;


import com.drrr.fluent.cralwer.core.SimpleContentsLoader;
import java.time.Duration;
import java.util.stream.IntStream;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

public class PoolTest {


    @Test
    @Disabled
    void pool테스트() throws Exception {

        var pool = create();

        pool.makeObject();

        var gpc = new GenericObjectPoolConfig<WebDriver>();
        gpc.setMaxTotal(2);

        try (var gop = new GenericObjectPool<>(create(), gpc)) {
            IntStream.range(1, 10)
                    .parallel()
                    .forEach(i -> {
                        WebDriver webDriver = null;
                        try {
                            webDriver = gop.borrowObject();
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }

                        System.out.println(i + 1);

                        webDriver.get("https://blog.dramancompany.com/page/" + (i + 1));
                        var wait = new WebDriverWait(webDriver, Duration.ofSeconds(5));

                        new SimpleContentsLoader(By.tagName("body")).waitUntilLoad(wait);
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }

                        gop.returnObject(webDriver);
                    });

            var wp = new WebDriverPool(create());

            // webdriver 사용에 대한 소유권은 누가 있나요?
            //
            wp.borrowObject();
            wp.returnObject(wp.borrowObject());

        }
    }

    BasePooledObjectFactory<WebDriver> create() {
        return new BasePooledObjectFactory<>() {
            @Override
            public WebDriver create() {
                return new FirefoxDriver();
            }

            @Override
            public PooledObject<WebDriver> wrap(WebDriver webDriver) {
                return new DefaultPooledObject<>(webDriver);
            }
        };
    }

    public static class WebDriverPool extends GenericObjectPool<WebDriver> {

        public WebDriverPool(PooledObjectFactory<WebDriver> factory) {
            super(factory);
        }
    }

}
