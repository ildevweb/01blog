package com.example.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.app.entity.Notification;
import com.example.app.entity.User;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    int countByToUser(User toUser);
}