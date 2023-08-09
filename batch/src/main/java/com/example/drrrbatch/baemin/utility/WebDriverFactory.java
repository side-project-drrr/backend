package com.example.drrrbatch.baemin.utility;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class WebDriverFactory {

    public WebDriver createDriver() {
        System.setProperty("webdriver.chrome.driver", "C:/Users/USER/chrome/chromedriver.exe");

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--disable-popup-blocking");       //팝업안띄움
        options.addArguments("headless");                       //브라우저 안띄움
        options.addArguments("--disable-gpu");            //gpu 비활성화
        options.addArguments("--blink-settings=imagesEnabled=false"); //이미지 다운 안받음
        options.addArguments("--user-data-dir=" + System.getProperty("java.io.tmpdir")); //ChromeDriver가 Chrome한테 SIGTERM 신호를 먼저 보냄
        WebDriver driver = new ChromeDriver(options);

        return driver;
    }

    public void closeDriver(WebDriver driver) {
        driver.quit();
    }

    public WebDriverWait createDriverWait(WebDriver driver) {
        return new WebDriverWait(driver, Duration.ofSeconds(10));
    }

}
