package com.drrr.infra.notifications.kafka.config;

import java.net.SocketTimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.BackOff;
import org.springframework.util.backoff.FixedBackOff;

@Slf4j
@Configuration
public class KafkaConfig {
    //재시도 간격 (milliseconds)
    private final static Long interval = 1000L;

    //Error로 남기기 전 Max 재시도 값
     private final static Long maxAttempts = 3L;

    @Bean
    public DefaultErrorHandler errorHandler() {
        //consumer는 fixed time만큼 기다리고 메세지 재소비를 실행
        final BackOff fixedBackOff = new FixedBackOff(interval, maxAttempts);
        //consumerRecord: 카프카 에러 기록을 담는다
        //exception: exception 종류
        //Max 재시도가 끝나고도 에러가 나면 해당 람다가 실행됨
        final DefaultErrorHandler errorHandler = new DefaultErrorHandler((consumerRecord, exception) -> {
            log.error("Message Consume Error");
            log.error("Record -> {}", consumerRecord);
            log.error("Exception -> {}", exception);
        }, fixedBackOff);
        //해당 에러는 시도함
        errorHandler.addRetryableExceptions(SocketTimeoutException.class);
        //해당 에러는 시도하지 않음
        errorHandler.addNotRetryableExceptions(NullPointerException.class);
        return errorHandler;
    }
}
