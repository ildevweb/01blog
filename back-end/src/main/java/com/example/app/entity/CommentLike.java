package com.example.app.entity;

import jakarta.persistence.*;

@Entity
@Table(
    name = "comment_like",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"comment_id", "user_id"})
    }
)
public class CommentLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Comment getComment() {
        return this.comment;
    }
    public User getUser() {
        return this.user;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }
    public void setUser(User user) {
        this.user = user;
    }
}
