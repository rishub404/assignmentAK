package com.example.grid07.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class NotificationSweeper {

    private final StringRedisTemplate redisTemplate;

    public NotificationSweeper(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Scheduled(cron = "0 */5 * * * *") // Runs every 5 minutes
    public void sweepNotifications() {
        // Find all keys matching the pending notifications pattern
        Set<String> keys = redisTemplate.keys("user:*:pending_notifs");
        if (keys == null || keys.isEmpty()) return;

        for (String key : keys) {
            Long size = redisTemplate.opsForList().size(key);
            if (size != null && size > 0) {
                // Pop all messages to summarize
                List<String> messages = redisTemplate.opsForList().range(key, 0, -1);
                redisTemplate.delete(key); // Clear the list

                String userId = key.split(":")[1]; // Extract ID from "user:{id}:pending_notifs"

                // Summarize per assignment instructions
                System.out.println("Summarized Push Notification: " + messages.get(0) + " and [" + (size - 1) + "] others interacted with your posts. (User " + userId + ")");
            }
        }
    }
}