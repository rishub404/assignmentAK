package com.example.grid07.service;

import com.example.grid07.dto.CreateCommentRequest;
import com.example.grid07.entity.Comment;
import com.example.grid07.entity.Post;
import com.example.grid07.exception.RateLimitException; // We will create this next
import com.example.grid07.repository.BotRepository;
import com.example.grid07.repository.CommentRepository;
import com.example.grid07.repository.PostRepository;
import com.example.grid07.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private BotRepository botRepository;

    @Autowired
    private GuardrailService guardrailService;

    @Autowired
    private ViralityService viralityService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserRepository userRepository;

    public Comment createComment(Long postId, CreateCommentRequest request) {
        // 1. Fetch the Post
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        // 2. Calculate Depth & Find Target Author
        Integer depthLevel = 0;
        Long targetAuthorId = post.getAuthorId(); // Default target is the post author

        if (request.getParentCommentId() != null) {
            Comment parent = commentRepository.findById(request.getParentCommentId())
                    .orElseThrow(() -> new IllegalArgumentException("Parent comment not found"));
            depthLevel = parent.getDepthLevel() + 1;
            targetAuthorId = parent.getAuthorId(); // Target becomes the parent comment author
        }

        // 3. Check Vertical Cap Guardrail (Reject if depth_level > 20)
        if (depthLevel > 20) {
            throw new IllegalArgumentException("Vertical cap exceeded");
        }

        // 4. Identify if the incoming commenter is a Bot
        boolean isUser = userRepository.existsById(request.getAuthorId());
        boolean isBot = botRepository.existsById(request.getAuthorId());

        if (!isUser && !isBot) {
            throw new IllegalArgumentException("Author not found: Must be a valid User or Bot");
        }

        if (isBot) {
            // 5. Check Horizontal Cap Guardrail
            if (!guardrailService.checkHorizontalCap(postId)) {
                throw new RateLimitException("429 Too Many Requests");
            }

            // 6. Check Cooldown Guardrail (ONLY if interacting with a Human)
            boolean isTargetHuman = userRepository.existsById(targetAuthorId);

            if (isTargetHuman) {
                if (!guardrailService.checkBotHumanCooldown(request.getAuthorId(), targetAuthorId)) {
                    throw new RateLimitException("Bot is on cooldown");
                }
            }
        }

        // Inside CommentService.java createComment method, right before saving:
        if (isBot) {
            viralityService.addPoints(postId, 1);

            // Only notify if target is a human
            if (!botRepository.existsById(targetAuthorId)) {
                notificationService.handleBotInteraction(targetAuthorId, request.getAuthorId());
            }
        } else {
            viralityService.addPoints(postId, 50); // Human comment = 50 pts
        }

        // 7. Save and Return the Comment
        Comment newComment = Comment.builder()
                .postId(postId)
                .authorId(request.getAuthorId())
                .content(request.getContent())
                .depthLevel(depthLevel)
                .build();

        return commentRepository.save(newComment);
    }
}