package com.drrr.domain.redis.config;

import com.drrr.domain.category.entity.RedisCategory;
import com.drrr.domain.category.entity.RedisCategoryPosts;
import com.drrr.domain.category.entity.RedisCategoryPosts.CompoundCategoryId;
import com.drrr.domain.techblogpost.entity.RedisTechBlogPost;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration("redis configuration")
public class RedisConfig {

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory();
    }

    @Bean
    public <T> RedisTemplate<String, T> redisTemplate() {
        final RedisTemplate<String, T> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }

    @Bean
    public RedisTemplate<String, RedisCategory> redisCategoryTemplate() {
        RedisTemplate<String, RedisCategory> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(RedisCategory.class));
        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public RedisTemplate<CompoundCategoryId, RedisCategoryPosts> redisCategoryPostsTemplate() {
        RedisTemplate<CompoundCategoryId, RedisCategoryPosts> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory());
        template.setKeySerializer(new Jackson2JsonRedisSerializer<>(CompoundCategoryId.class));
        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(RedisCategoryPosts.class));
        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public RedisTemplate<String, RedisTechBlogPost> redisTechBlogPostTemplate() {
        RedisTemplate<String, RedisTechBlogPost> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(RedisTechBlogPost.class));
        template.afterPropertiesSet();
        return template;
    }


}