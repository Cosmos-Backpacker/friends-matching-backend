package com.cosmos.friendsMatching.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
public class RedisTemplateConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        //指定键为String 值为Value
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        //设置连接工厂（必须设置）
        redisTemplate.setConnectionFactory(factory);
        //设置Redis序列还器
        redisTemplate.setKeySerializer(RedisSerializer.string());
        return redisTemplate;
    }

}
