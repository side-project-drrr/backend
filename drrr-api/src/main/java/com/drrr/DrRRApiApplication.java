package com.drrr;

import com.drrr.infra.notifications.kafka.email.EmailProducer;
import jakarta.annotation.PostConstruct;
import java.util.TimeZone;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
@RequiredArgsConstructor
public class DrRRApiApplication {
    private final EmailProducer emailProducer;

    public static void main(String[] args) {
        SpringApplication.run(DrRRApiApplication.class, args);
    }

    @PostConstruct
    public void started() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
    }

//    @Scheduled(cron = "0 * * * * ?")
//    public void scheduled(){
//        emailProducer.sendMessage();
//    }
}
