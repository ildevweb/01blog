package com.example.app.entity;

import jakarta.persistence.*;

@Entity
@Table(
    name = "post_like",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"post_id", "user_id"})
    }
)
public class PostLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Post getPost() {
        return this.post;
    }
    public User getUser() {
        return this.user;
    }

    public void setPost(Post post) {
        this.post = post;
    }
    public void setUser(User user) {
        this.user = user;
    }
}
