package com.example.app.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;


@Entity
@Table(name = "posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;
    private String media;
    private Long ownerId;
    private Long time;

    public Post() {
    }

    public Post(String content, String media, Long ownerId, Long time) {
        this.content = content;
        this.media = media;
        this.ownerId = ownerId;
        this.time = time;
    }

    public Post(String content, Long ownerId, Long time) {
        this.content = content;
        this.ownerId = ownerId;
        this.time = time;
    }

    public Long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getOwnerId() {
        return this.ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public String getMedia() {
        return media;
    }
    
    public void setMedia(String media) {
        this.media = media;
    }

    public Long getTime() {
        return this.time;
    }

    public void setTime(Long time) {
        this.time = time;
    }
}
