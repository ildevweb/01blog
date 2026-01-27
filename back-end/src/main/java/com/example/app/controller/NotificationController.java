package com.example.app.controller;

import java.util.*;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.example.app.entity.Notification;
import com.example.app.entity.User;
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
}
