package com.example.app.dto;

public class PostInfos {
    private Long id;
    private String username;
    private String time;
    private String content;
    private String media;

    public PostInfos(Long id, String username, String time, String content, String media) {
        this.id = id;
        this.username = username;
        this.time = time;
        this.content = content;
        this.media = media;
    }

    public PostInfos(Long id, String username, String time, String content) {
        this.id = id;
        this.username = username;
        this.time = time;
        this.content = content;
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
    public String getMedia() {
        return this.media;
    }

}
