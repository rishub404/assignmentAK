package com.example.grid07.entity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.print.DocFlavor;
import java.time.LocalDateTime;

@Entity
@Table (name = "post")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long authorId;

    @Column(columnDefinition = "TEXT") // Varchar(255) limit can be exceeded for longer content
    private String content;

    @Builder.Default
    @CreationTimestamp
    private LocalDateTime createdAt = LocalDateTime.now();

}
