package com.example.app.controller;

import java.time.Instant;
import java.util.*;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.example.app.entity.User;
import com.example.app.dto.NotificationInfos;
import com.example.app.entity.Notification;
import com.example.app.repository.NotificationRepository;
import com.example.app.security.UserPrincipal;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "http://localhost:4200")
public class NotificationController {
    
    private final NotificationRepository notificationRepository;

    public NotificationController(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @GetMapping("/unread/count")
    public ResponseEntity<?> getCount() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal user = (UserPrincipal) auth.getPrincipal();
        User currentUser = user.getUser();

        int count = notificationRepository.countByToUser(currentUser);

        return ResponseEntity.ok(Map.of("count", count));
    }

    @GetMapping("/unread/get")
    public List<NotificationInfos> getUnreadNotifications() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
        User currentUser = userPrincipal.getUser();

        List<Notification> notifications = notificationRepository
                .findByToUserAndReaded(currentUser, false);

        return notifications.stream()
                .map(notif -> {
                    User fromUser = notif.getFromUser();

                    return new NotificationInfos(
                        notif.getId(),
                        fromUser.getName(),
                        timeAgo(notif.getTime()),
                        "has created a post",
                        notif.getReaded()
                    );
                })
                .toList();
    }


    public static String timeAgo(long postSeconds) {

        long now = Instant.now().getEpochSecond();
        long diff = now - postSeconds;

        if (diff < 0) {
            return "just now";
        }

        if (diff < 60) {
            return diff + " seconds ago";
        }

        long minutes = diff / 60;
        if (minutes < 60) {
            return minutes + " minutes ago";
        }

        long hours = minutes / 60;
        if (hours < 24) {
            return hours + " hours ago";
        }

        long days = hours / 24;
        if (days < 30) {
            return days + " days ago";
        }

        long months = days / 30;
        if (months < 12) {
            return months + " months ago";
        }

        long years = months / 12;
        return years + " years ago";
    }
}
