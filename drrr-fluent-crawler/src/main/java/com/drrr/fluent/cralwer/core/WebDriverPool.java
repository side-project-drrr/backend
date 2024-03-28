package com.drrr.fluent.cralwer.core;

import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.DestroyMode;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

public class WebDriverPool extends GenericObjectPool<WebDriver> {
    private static final int MAX_TOTAL = 10;

    public WebDriverPool(PooledObjectFactory<WebDriver> factory) {
        super(factory);
        this.setMaxTotal(MAX_TOTAL);
    }

    public WebDriverPool(PooledObjectFactory<WebDriver> factory, int max) {
        super(factory);
        this.setMaxTotal(max);
    }

    public WebDriverPool maxTotal(int maxTotal) {
        this.setMaxTotal(maxTotal);
        return this;
    }


    public <T> T delegate(Function<WebDriver, T> function) {
        try {
            var webdriver = this.borrowObject();
            var result = function.apply(webdriver);
            this.returnObject(webdriver);

            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public WebDriver borrow() {
        try {
            return this.borrowObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {
        super.close();
    }

    @RequiredArgsConstructor
    public static class WebDriverPoolFactory extends BasePooledObjectFactory<WebDriver> {
        private final FirefoxOptions firefoxOptions;

        @Override
        public WebDriver create() {
            return new FirefoxDriver(firefoxOptions);
        }

        @Override
        public PooledObject<WebDriver> wrap(WebDriver webDriver) {
            return new DefaultPooledObject<>(webDriver);
        }

        @Override
        public void destroyObject(PooledObject<WebDriver> p, DestroyMode destroyMode) throws Exception {
            p.getObject().close();
        }
    }
}
