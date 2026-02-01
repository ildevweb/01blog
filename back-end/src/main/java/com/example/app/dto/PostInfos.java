package com.example.app.dto;

public class PostInfos {
    private Long id;
    private String username;
    private String time;
    private String content;
    private String media;
    private boolean liked;
    private Long count;
    private boolean mine;
    private String status;

    public PostInfos(Long id, String username, String time, String content, String media, boolean liked, Long count, boolean mine, String status) {
        this.id = id;
        this.username = username;
        this.time = time;
        this.content = content;
        this.media = media;
        this.liked = liked;
        this.count = count;
        this.mine = mine;
        this.status = status;
    }

    public PostInfos(Long id, String username, String time, String content, boolean liked, Long count, boolean mine, String status) {
        this.id = id;
        this.username = username;
        this.time = time;
        this.content = content;
        this.liked = liked;
        this.count = count;
        this.mine = mine;
        this.status = status;
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
    public boolean getMine() {
        return this.mine;
    }
    public String getStatus() {
        return this.status;
    }
}
