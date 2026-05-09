package com.example.grid07.constant;

public class RedisConstants {

    // Private constructor to prevent instantiation (best practice for constant classes)
    private RedisConstants() {}

    // 1. Horizontal Cap Key
    public static final String POST_BOT_COUNT_KEY = "post:%d:bot_count";


    public static final String POST_VIRALITY_SCORE = "post:%d:virality_score";


    public static final String BOT_HUMAN_COOLDOWN = "cooldown:bot_%d:human_%d";


    public static final String NOTIFICATION_LIST = "user:%d:pending_notifs";


    public static final String NOTIFICATION_COOLDOWN = "notif_cooldown:user_%d";

}