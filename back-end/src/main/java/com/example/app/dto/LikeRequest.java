package com.example.app.dto;

public class LikeRequest {
    private Long postId;
    private Long commentId;


    public Long getPostId() { return postId; }
    public void setPostId(Long postId) { this.postId = postId; }

    public Long getCommentId() { return commentId; }
    public void setCommentId(Long commentId) { this.commentId = commentId; }
}
