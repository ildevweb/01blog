package com.example.app.dto;

public class CommentRequest {
    private String content;
    private Long postId;

    public void setContent(String content) {
        this.content = content;
    }
    public String getContent() {
        return this.content;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }
    public Long getPostId() {
        return this.postId;
    }
}
