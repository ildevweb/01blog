package com.example.app.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LikeRequest {
    private Long postId;
    private Long commentId;
    private Long userId;
}
