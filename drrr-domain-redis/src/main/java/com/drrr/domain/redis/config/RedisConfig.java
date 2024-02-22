package com.drrr.domain.redis.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;

@Configuration("redis configuration")
public class RedisConfig {

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory();
    }
    // RedisTemplate을 사용하여 Redis에 데이터를 저장하고 조회할 수 있음
    // key로는 CompoundPostCategoriesSliceId, value로는 RedisAllPostCategoriesSlice를 사용
    // RedisTemplate bean를 만들어줘
    // key로는 String으로 넣고 value는 RedisAllPostCategoriesSlice를 사용하지만 byte[]로 변환해서 넣어줘

    @Bean
    public <K, T> RedisTemplate redisTemplate() {
        final RedisTemplate<K, T> template = new RedisTemplate<>();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // Java 8 날짜/시간 모듈 등록
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // 날짜/시간을 timestamp로 쓰지 않도록 설정
        template.setConnectionFactory(redisConnectionFactory());
        template.setKeySerializer(new GenericJackson2JsonRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        //   template.setHashKeySerializer(new GenericJackson2JsonRedisSerializer());
        //  template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }
}