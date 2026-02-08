package com.example.app.dto;

public class UserInfos {
    private Long id;
    private String username;
    private String email;
    private int followers;
    private int followeds;
    private boolean followed;
    private String status;

    public UserInfos(Long id, String username, String email, int followers, int followeds, boolean followed, String status) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.followers = followers;
        this.followeds = followeds;
        this.followed = followed;
        this.status = status;
    }

    public UserInfos(String username) {
        this.username = username;
    }

    public Long getId() {
        return this.id;
    }
    public String getUsername() {
        return this.username;
    }
    public String getEmail() {
        return this.email;
    }
    public boolean getFollowed() {
        return this.followed;
    }
    public int getFollowers() {
        return this.followers;
    }
    public int getFolloweds() {
        return this.followeds;
    }
    public String getStatus() {
        return this.status;
    }

}
