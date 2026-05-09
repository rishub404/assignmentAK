package com.example.grid07.dto;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCommentRequest {

    private String content;

    private Long authorId;

    private Long parentCommentId;

}
