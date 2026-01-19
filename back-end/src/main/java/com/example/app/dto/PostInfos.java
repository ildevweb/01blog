package com.example.app.dto;

public class PostInfos {
    private Long id;
    private String username;
    private String time;
    private String content;
    private String media;
    private boolean liked;
    private Long count;

    public PostInfos(Long id, String username, String time, String content, String media, boolean liked, Long count) {
        this.id = id;
        this.username = username;
        this.time = time;
        this.content = content;
        this.media = media;
        this.liked = liked;
        this.count = count;
    }

    public PostInfos(Long id, String username, String time, String content, boolean liked, Long count) {
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
    public String getMedia() {
        return this.media;
    }
    public boolean getLiked() {
        return this.liked;
    }
    public Long getCount() {
        return this.count;
    }

}
