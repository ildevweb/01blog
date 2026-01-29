package com.example.app.entity;

import jakarta.persistence.*;

@Entity
@Table(
    name = "notification",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"from_user", "to_user", "readed"})
    }
)
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_user", nullable = false)
    private User fromUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_user", nullable = false)
    private User toUser;

    private boolean readed;

    private Long time;

    public Notification() {}

    public Notification(User fromUser, User toUser, boolean readed, Long time) {
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.readed = readed;
        this.time = time;
    }

    public Long getId() {
        return this.id;
    }

    public User getFromUser() {
        return this.fromUser;
    }
    public User getToUser() {
        return this.toUser;
    }
    public boolean getReaded() {
        return this.readed;
    }
    public Long getTime() {
        return this.time;
    }

    public void setFromUser(User fromUser) {
        this.fromUser = fromUser;
    }
    public void setToUser(User toUser) {
        this.toUser = toUser;
    }
    public void setReaded(boolean readed) {
        this.readed = readed;
    }
    public void setTime(Long time) {
        this.time = time;
    }
}
