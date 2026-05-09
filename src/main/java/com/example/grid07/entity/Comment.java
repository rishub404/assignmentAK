package com.example.grid07.entity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table (name = "comment")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long postId;

    private Long authorId;

    @Column(columnDefinition = "TEXT")
    private String content;

    private Integer depthLevel;

    @Builder.Default
    @CreationTimestamp
    private LocalDateTime createdAt = LocalDateTime.now();
}
