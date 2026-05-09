package com.example.grid07.service;

import com.example.grid07.constant.RedisConstants;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

@Service
public class NotificationService {

    private final StringRedisTemplate redisTemplate;

    public NotificationService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void handleBotInteraction(Long targetUserId, Long botId) {
        String cooldownKey = String.format(RedisConstants.NOTIFICATION_COOLDOWN, targetUserId);
        String listKey = String.format(RedisConstants.NOTIFICATION_LIST, targetUserId);

        // Try to set the 15-minute lock. If it succeeds (returns true), they aren't on cooldown.
        Boolean isAllowed = redisTemplate.opsForValue().setIfAbsent(cooldownKey, "locked", 15, TimeUnit.MINUTES);

        if (Boolean.TRUE.equals(isAllowed)) {
            System.out.println("Push Notification Sent to User: Bot " + botId + " interacted with your post.");
        } else {
            // User is on cooldown, push to pending list
            String message = "Bot " + botId + " replied to your post";
            redisTemplate.opsForList().rightPush(listKey, message);
        }
    }
}