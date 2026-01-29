package com.example.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.app.entity.Notification;
import com.example.app.entity.User;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    int countByToUserAndReaded(User toUser, Boolean readed);

    Notification findByToUser(User toUser);

    List<Notification> findByToUserAndReaded(User toUser, Boolean readed);
}