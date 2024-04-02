package com.drrr.fluent.cralwer.core;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class SimpleContentsLoader implements ContentsLoader {
    private final By target;

    public SimpleContentsLoader(By target) {
        this.target = target;
    }

    @Override
    public void waitUntilLoad(WebDriverWait webDriverWait) {
        webDriverWait.until(
                ExpectedConditions.visibilityOfElementLocated(target)
        );
    }
}
