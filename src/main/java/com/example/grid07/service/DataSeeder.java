package com.example.grid07.service;

import com.example.grid07.entity.Bot;
import com.example.grid07.entity.User;
import com.example.grid07.repository.BotRepository;
import com.example.grid07.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final BotRepository botRepository;

    public DataSeeder(UserRepository userRepository, BotRepository botRepository) {
        this.userRepository = userRepository;
        this.botRepository = botRepository;
    }

    @Override
    public void run(String... args) {
        // Save User #1
        if (userRepository.count() == 0) {
            User human = User.builder().username("rishav_human").isPremium(false).build();
            userRepository.save(human);
            System.out.println("✅ Seeded Human in Database");
        }

        // Save Bot #2 (We save a dummy first so the real bot gets ID #2 to avoid overlapping with User #1)
        if (botRepository.count() == 0) {
            botRepository.save(Bot.builder().name("dummy").personaDescription("skip").build());
            botRepository.save(Bot.builder().name("spam_bot_9000").personaDescription("Aggressive replier").build());
            System.out.println("✅ Seeded Bot in Database");
        }
    }
}