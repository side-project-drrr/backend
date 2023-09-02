package com.example.drrrapi;

import jakarta.annotation.PostConstruct;
import java.util.TimeZone;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DrRRApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(DrRRApiApplication.class, args);
    }
    /**
     * 서버 타임존 설정
     */
    @PostConstruct
    public void started() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

}
