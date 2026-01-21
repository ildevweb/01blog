package com.example.app.entity;

import jakarta.persistence.*;

@Entity
@Table(
    name = "follow",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"followed", "follower"})
    }
)
public class Follow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "followed", nullable = false)
    private User followed;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follower", nullable = false)
    private User follower;

    public User getFollowed() {
        return this.followed;
    }
    public User getFollower() {
        return this.follower;
    }

    public void setFollowed(User followed) {
        this.followed = followed;
    }
    public void setFollower(User follower) {
        this.follower = follower;
    }
}
