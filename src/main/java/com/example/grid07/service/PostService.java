package com.example.grid07.service;

import com.example.grid07.dto.CreatePostRequest;
import com.example.grid07.entity.Post;
import com.example.grid07.repository.BotRepository;
import com.example.grid07.repository.PostRepository;
import com.example.grid07.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PostService {


    @Autowired
    private PostRepository postRepository;

    @Autowired
    private BotRepository botRepository;

    @Autowired
    private UserRepository userRepository;

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