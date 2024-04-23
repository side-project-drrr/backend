package com.drrr.property;


import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Objects;
import org.apache.logging.log4j.util.Strings;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;

@ConfigurationProperties("driver")
@ConfigurationPropertiesBinding
public record DriverProperty(
        String url,
        String type,
        String[] options
) {
    public DriverProperty {
        validateLocalNonRequireUrl(type, url);
        validateNonTypeNonRequireUrl(type, url);
        validateRemoteRequireUrl(type, url);
    }

    private static void validateLocalNonRequireUrl(String type, String url) {
        if (Objects.equals(type, "local")) {
            if (Objects.isNull(url)) {
                return;
            }
            if (!url.isBlank()) {
                throw new IllegalArgumentException("local 타입일 때 url을 설정할 수 없습니다");
            }
        }
    }

    private static void validateNonTypeNonRequireUrl(String type, String url) {
        if (Objects.isNull(type)) {
            if (Objects.isNull(url)) {
                return;
            }
            if (!url.isBlank()) {

                throw new IllegalArgumentException("type을 설정하지 않았을 때 url을 설정할 수 없습니다.");
            }
        }
    }

    private static void validateRemoteRequireUrl(String type, String url) {
        if (Objects.equals(type, "remote")) {
            if (Objects.isNull(url) || Strings.isBlank(url)) {
                throw new IllegalArgumentException("remote type에서 url을 설정해야 합니다.");
            }
        }
    }

    public boolean isRemote() {
        return Objects.equals(type, "remote");
    }

    public FirefoxOptions getOptions() {
        return new FirefoxOptions()
                .setPageLoadStrategy(PageLoadStrategy.EAGER)
                .addArguments(Arrays.asList(options));
    }

    public URL getUrl() {
        try {
            return new URL(this.url);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public WebDriver createWebDriver() {
        if (isRemote()) {
            return new RemoteWebDriver(getUrl(), getOptions());
        }

        return new FirefoxDriver(getOptions());
    }
}
