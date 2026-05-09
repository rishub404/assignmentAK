package com.example.grid07.dto;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePostRequest {

    private Long authorId;

    private String content;
}
