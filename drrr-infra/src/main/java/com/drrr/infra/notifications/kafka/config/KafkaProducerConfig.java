package com.drrr.infra.notifications.kafka.config;

import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

@Configuration
public class KafkaProducerConfig {
    private final String producerHost;

    public KafkaProducerConfig(@Value("${spring.kafka.producer.bootstrap-servers}") final String producerHost) {
        this.producerHost = producerHost;
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        final Map<String, Object> prop = new HashMap<>();
        //Kafka 서버의 위치를 설정
        prop.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, producerHost);
        //Kafka message key에 대한 직렬화 클래스로 JsonSerializer를 지정
        prop.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        //Kafka message value에 대한 직렬화 클래스로 JsonSerializer를 지정
        prop.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        //메세지를 중복없이 단 1개의 메시지를 소비시키게 하기 위한 설정
        /**
         * 멱등성 설정과 트랜잭션 설정으로 인해 카프카는 트랜잭션 id를 사용해서 producer가 보내는
         */
       // prop.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
        //prop.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, "prod-1");


        return new DefaultKafkaProducerFactory<>(prop);
    }




}