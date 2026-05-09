package com.example.grid07.controller;

import com.example.grid07.dto.CreateCommentRequest;
import com.example.grid07.dto.CreatePostRequest;
import com.example.grid07.entity.Comment;
import com.example.grid07.entity.Post;
import com.example.grid07.service.CommentService;
import com.example.grid07.service.PostService;
import com.example.grid07.service.ViralityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;
    private final CommentService commentService;
    private final ViralityService viralityService;

    public PostController(PostService postService, CommentService commentService, ViralityService viralityService) {
        this.postService = postService;
        this.commentService = commentService;
        this.viralityService = viralityService;
    }

    // 1. Create a Post
    @PostMapping
    public ResponseEntity<Post> createPost(@RequestBody CreatePostRequest request) {
        Post post = postService.createPost(request);
        return new ResponseEntity<>(post, HttpStatus.CREATED);
    }

    // 2. Add a Comment
    @PostMapping("/{postId}/comments")
    public ResponseEntity<Comment> createComment(
            @PathVariable Long postId,
            @RequestBody CreateCommentRequest request) {
        Comment comment = commentService.createComment(postId, request);
        return new ResponseEntity<>(comment, HttpStatus.CREATED);
    }

    // 3. Like a Post (Stateless routing to satisfy the endpoint requirement)
    // Inside PostController.java
    @PostMapping("/{postId}/like")
    public ResponseEntity<String> likePost(@PathVariable Long postId, @RequestParam Long authorId) {
        // Human like = 20 pts (Assuming only humans can like based on instructions)
        viralityService.addPoints(postId, 20);
        return ResponseEntity.ok("Post liked successfully");
    }
}