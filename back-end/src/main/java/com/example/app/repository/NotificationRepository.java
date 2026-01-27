package com.example.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.app.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}