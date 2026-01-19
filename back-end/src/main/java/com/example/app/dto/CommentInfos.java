package com.example.app.dto;

public class CommentInfos {
    private Long id;
    private String username;
    private String time;
    private String content;
    private boolean liked;
    private Long count;

    public CommentInfos(Long id, String username, String time, String content, boolean liked, Long count) {
        this.id = id;
        this.username = username;
        this.time = time;
        this.content = content;
        this.liked = liked;
        this.count = count;
    }

    public Long getId() {
        return this.id;
    }
    public String getUsername() {
        return this.username;
    }
    public String getTime() {
        return this.time;
    }
    public String getContent() {
        return this.content;
    }
    public boolean getLiked() {
        return this.liked;
    }
    public Long getCount() {
        return this.count;
    }

}
