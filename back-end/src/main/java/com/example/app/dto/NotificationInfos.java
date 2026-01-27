package com.example.app.dto;

public class NotificationInfos {
    private Long id;
    private String username;
    private String time;
    private String content;
    private boolean readed;

    public NotificationInfos(Long id, String username, String time, String content, boolean readed) {
        this.id = id;
        this.username = username;
        this.time = time;
        this.content = content;
        this.readed = readed;
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
    public boolean getReaded() {
        return this.readed;
    }
}
