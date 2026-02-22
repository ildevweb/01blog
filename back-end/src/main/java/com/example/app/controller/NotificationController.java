package com.example.app.controller;

import java.util.*;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import com.example.app.dto.NotificationInfos;
import com.example.app.service.NotificationService;
import lombok.AllArgsConstructor;


@AllArgsConstructor
@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "http://localhost:4200")
public class NotificationController {
    
    private final NotificationService notificationService;


    @GetMapping("/unread/count")
    public ResponseEntity<?> getCount() {
        return notificationService.getCount();
    }

    @GetMapping("/unread/get")
    public List<NotificationInfos> getUnreadNotifications() {
        return notificationService.getUnreadNotifications();
    }


    @GetMapping("/unread/mark_all_as_read")
    public void markAllAsRead() {
        notificationService.markAllAsRead();        
    }

    @GetMapping("/toggle_read/{id}")
    public ResponseEntity<?> toggleRead(@PathVariable Long id) {
        return notificationService.toggleRead(id);
    }
}
