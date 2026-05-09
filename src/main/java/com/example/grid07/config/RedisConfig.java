package com.example.grid07.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
public class RedisConfig {

//    @Bean
//    LettuceConnectionFactory redisConnectionFactory() {
//        return new LettuceConnectionFactory();
//    }
// it will automatically read application.properties and create connection factory in the background

    @Bean
    StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory){
        //Using StringRedisTemplate as value=key=string works fine for simple increment counter
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }
}
