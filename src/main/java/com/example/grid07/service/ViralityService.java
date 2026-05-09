package com.example.grid07.service;

import com.example.grid07.constant.RedisConstants;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class ViralityService {

    private final StringRedisTemplate redisTemplate;

    public ViralityService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void addPoints(Long postId, int points) {
        String key = String.format(RedisConstants.POST_VIRALITY_SCORE, postId);
        redisTemplate.opsForValue().increment(key, points);
    }
}