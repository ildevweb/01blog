package com.example.app.dto;

public class PostInfos {
    private String username;
    private String time;
    private String content;
    private String media;

    public PostInfos(String username, String time, String content, String media) {
        this.username = username;
        this.time = time;
        this.content = content;
        this.media = media;
    }

    public PostInfos(String username, String time, String content) {
        this.username = username;
        this.time = time;
        this.content = content;
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
