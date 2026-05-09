package com.example.grid07.service;

import com.example.grid07.constant.RedisConstants;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class GuardrailService {

    private final StringRedisTemplate redisTemplate;

    public GuardrailService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean checkHorizontalCap(Long postId) {
        String key = String.format(RedisConstants.POST_BOT_COUNT_KEY, postId);
        //Atomically increment the key using redisTemplate.opsForValue().increment(key)
        //If the incremented value is <= 100, return true. Otherwise, return false.
        Long currentVal = redisTemplate.opsForValue().increment(key);
        return currentVal != null && currentVal <=100;
    }

    public boolean checkVerticalCap(Integer parentDepthLevel) {
        //If parentDepthLevel is < 20, return true else false.
        return parentDepthLevel == null || parentDepthLevel < 20;
    }

    public boolean checkBotHumanCooldown(Long botId, Long humanId) {
        String key = String.format(RedisConstants.BOT_HUMAN_COOLDOWN, botId, humanId);
        //Set the key using setIfAbsent with a 10-minute timeout.
        //Return the boolean result.

        return Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(key, "locked", 10, TimeUnit.MINUTES));
        //To avoid NullPointerException
    }
}