package com.drrr.domain.redis.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration("redis configuration")
public class RedisConfig {

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory();
    }

    @Bean
    public RedisTemplate redisTemplate() {
        final RedisTemplate<Object, Object> template = new RedisTemplate<>();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // Java 8 날짜/시간 모듈 등록
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // 날짜/시간을 timestamp로 쓰지 않도록 설정
        template.setConnectionFactory(redisConnectionFactory());
//        template.setKeySerializer(new GenericJackson2JsonRedisSerializer());
//        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(objectMapper, RedisAllPostCategoriesSlice.class));
//        template.setKeySerializer(new StringRedisSerializer());
//        template.setValueSerializer(StringRedisSerializer.UTF_8);

        return template;
    }
}