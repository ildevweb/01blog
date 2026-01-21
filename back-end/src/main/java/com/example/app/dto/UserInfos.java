package com.example.app.dto;

public class UserInfos {
    private Long id;
    private String username;
    private int followers;
    private int followeds;
    private boolean followed;

    public UserInfos(Long id, String username, int followers, int followeds, boolean followed) {
        this.id = id;
        this.username = username;
        this.followers = followers;
        this.followeds = followeds;
        this.followed = followed;
    }

    public Long getId() {
        return this.id;
    }
    public String getUsername() {
        return this.username;
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

}
