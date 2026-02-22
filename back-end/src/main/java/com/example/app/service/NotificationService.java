package com.example.app.service;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import com.example.app.security.UserPrincipal;
import com.example.app.dto.NotificationInfos;
import com.example.app.entity.User;
import com.example.app.entity.Notification;
import com.example.app.repository.NotificationRepository;
import java.util.Map;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;

    
    public ResponseEntity<?> getCount() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal user = (UserPrincipal) auth.getPrincipal();
        User currentUser = user.getUser();

        int count = notificationRepository.countByToUserAndReaded(currentUser, false);

        return ResponseEntity.ok(Map.of("count", count));
    }

    public List<NotificationInfos> getUnreadNotifications() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
        User currentUser = userPrincipal.getUser();

        List<Notification> notifications = notificationRepository
                .findByToUserAndReadedOrderByTimeDesc(currentUser, false);

        return notifications.stream()
                .map(notif -> {
                    User fromUser = notif.getFromUser();

                    return new NotificationInfos(
                        notif.getId(),
                        fromUser.getName(),
                        fromUser.getId(),
                        PostService.timeAgo(notif.getTime()),
                        "has created a post",
                        notif.getReaded()
                    );
                })
                .toList();
    }

    public void markAllAsRead() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal user = (UserPrincipal) auth.getPrincipal();
        User currentUser = user.getUser();

        List<Notification> notifications = notificationRepository.findByToUser(currentUser);

        for (Notification notification : notifications) {
            notification.setReaded(true);

            notificationRepository.save(notification);
        }
    }

    public ResponseEntity<?> toggleRead(Long id) {
        Optional<Notification> notification = notificationRepository.findById(id);

        
        if (notification.isPresent()) {
            Notification notif = notification.get();
            boolean readed = notif.getReaded();
            notif.setReaded(!readed);
            notificationRepository.save(notif);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "readed", !readed
            ));
        }

        return ResponseEntity.ok(Map.of(
            "success", false,
            "message", "notification not found"
        ));
    }
}
