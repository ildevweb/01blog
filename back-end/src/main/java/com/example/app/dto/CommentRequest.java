package com.example.app.dto;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class CommentRequest {
    private String content;
    private Long postId;
}
