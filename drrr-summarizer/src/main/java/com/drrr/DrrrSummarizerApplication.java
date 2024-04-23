package com.drrr;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan(basePackages = "com.drrr")
public class DrrrSummarizerApplication {

    public static void main(String... args) {
        SpringApplication.run(DrrrSummarizerApplication.class, args);
    }
}
