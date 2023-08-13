package com.example.drrrbatch.batch.config.crawler;


import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@Slf4j
@ConfigurationProperties(prefix = "chromedriver.path")
public class ChromeDriverPathProperty {
    private String mac;
    private String window;

    public String getDriverPath() {
        final String osName = System.getProperty("os.name").toLowerCase();
        log.info("현재 실행 환경 {}", osName);

        if (osName.contains("mac")) {
            return mac;
        }
        if (osName.contains("window")) {
            return window;
        }
        return "/usr/local/bin/chromedriver";
    }
}
