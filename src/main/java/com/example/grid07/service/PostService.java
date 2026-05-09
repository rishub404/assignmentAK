package com.example.grid07.service;

import com.example.grid07.dto.CreatePostRequest;
import com.example.grid07.entity.Post;
import com.example.grid07.repository.BotRepository;
import com.example.grid07.repository.PostRepository;
import com.example.grid07.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final BotRepository botRepository;
    private final UserRepository userRepository;

    public PostService(PostRepository postRepository,
                       BotRepository botRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.botRepository = botRepository;
        this.userRepository = userRepository;
    }

    // Notice we only need the request DTO here
    public Post createPost(CreatePostRequest request) {

        Long authorId = request.getAuthorId();

        // Check if the author exists in EITHER table
        boolean isUser = userRepository.existsById(authorId);
        boolean isBot = botRepository.existsById(authorId);

        if (!isUser && !isBot) {
            throw new IllegalArgumentException("Author not found: Must be a valid User or Bot");
        }

        // Build and save the post
        Post newPost = Post.builder()
                .authorId(authorId)
                .content(request.getContent())
                .build();

        return postRepository.save(newPost);
    }
}