package com.example.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.example.app.entity.Notification;
import com.example.app.entity.User;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    int countByToUserAndReaded(User toUser, Boolean readed);

    Notification findByToUser(User toUser);

    List<Notification> findByToUserAndReaded(User toUser, Boolean readed);

    @Modifying
    @Transactional
    @Query("""
        DELETE FROM Notification n
        WHERE n.fromUser.id = :userId
        OR n.toUser.id = :userId
    """)
    void deleteByFromUserOrToUser(@Param("userId") Long userId);
}