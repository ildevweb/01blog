package com.example.app.dto;

public class UserInfos {
    private Long id;
    private String username;
    private boolean followed;

    public UserInfos(Long id, String username, boolean followed) {
        this.id = id;
        this.username = username;
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

}
