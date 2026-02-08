package com.example.app.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import com.example.app.dto.UserInfos;
import com.example.app.entity.Report;
import com.example.app.entity.User;
import com.example.app.repository.FollowRepository;
import com.example.app.repository.NotificationRepository;
import com.example.app.repository.PostLikeRepository;
import com.example.app.repository.ReportRepository;
import com.example.app.repository.UserRepository;
import com.example.app.security.UserPrincipal;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final FollowService followService;
    private final FollowRepository followRepository;
    private final PostLikeRepository postLikeRepository;
    private final NotificationRepository notificationRepository;
    private final ReportRepository reportRepository;

    public UserService(UserRepository userRepository, FollowService followService, FollowRepository followRepository, PostLikeRepository postLikeRepository, NotificationRepository notificationRepository, ReportRepository reportRepository) {
        this.userRepository = userRepository;
        this.followService = followService;
        this.followRepository = followRepository;
        this.postLikeRepository = postLikeRepository;
        this.notificationRepository = notificationRepository;
        this.reportRepository = reportRepository;
    }
    
    public List<UserInfos> getAllUsers(int page, int size) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userp = (UserPrincipal) auth.getPrincipal();
        Long userId = userp.getId();
        User user = userp.getUser();

        String role = user.getRole();

        Pageable pageable = PageRequest.of(page, size);

        Page<User> users;
        if (role.equals("admin")) {
            users = userRepository.findByIdNot(userId, pageable);
        } else {
            users = userRepository.findByIdNotAndStatus(userId, "active", pageable);
        }

        return users.stream()
            .map(usr -> new UserInfos(
                usr.getId(),
                usr.getName(),
                usr.getEmail(),
                followService.countFollowers(usr.getId()),
                followService.countFolloweds(usr.getId()),
                followService.isFollowing(userId, usr.getId()),
                usr.getStatus()
            ))
            .toList();

    }

    public UserInfos getProfile(Long userId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
        User currentUser = userPrincipal.getUser();

        return userRepository.findById(userId)
            .map(user -> new UserInfos(
                user.getId(),
                user.getName(),
                user.getEmail(),
                followService.countFollowers(user.getId()),
                followService.countFolloweds(user.getId()),
                followService.isFollowing(currentUser.getId(), user.getId()),
                user.getStatus()
            ))
            .orElse(new UserInfos("deleted user"));
    }


    public List<UserInfos> getFollowers(Long userId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal user = (UserPrincipal) auth.getPrincipal();


        return followRepository.findByFollowedId(userId)
            .stream()
            .map(follow -> {
                User usr = follow.getFollower();

                return new UserInfos(
                    usr.getId(),
                    usr.getName(),
                    usr.getEmail(),
                    followService.countFollowers(usr.getId()),
                    followService.countFolloweds(usr.getId()),
                    followService.isFollowing(user.getId(), usr.getId()),
                    usr.getStatus()
                );
            })
            .toList();
    }

    public List<UserInfos> getFolloweds(Long userId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal user = (UserPrincipal) auth.getPrincipal();


        return followRepository.findByFollowerId(userId)
            .stream()
            .map(follow -> {
                User usr = follow.getFollowed();

                return new UserInfos(
                    usr.getId(),
                    usr.getName(),
                    usr.getEmail(),
                    followService.countFollowers(usr.getId()),
                    followService.countFolloweds(usr.getId()),
                    followService.isFollowing(user.getId(), usr.getId()),
                    usr.getStatus()
                );
            })
            .toList();
    }


    public ResponseEntity<?> deleteUser(Long userId) {

        if (!userRepository.existsById(userId)) {
            return ResponseEntity.ok(Map.of(
                "success", false,
                "message", "User doesn't exist"
            ));
        }


        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userp = (UserPrincipal) auth.getPrincipal();
        User user = userp.getUser();

        if (!user.getRole().equals("admin")) {
            return ResponseEntity.ok(Map.of(
                "success", false,
                "message", "Only admin can delete users"
            ));
        }

        postLikeRepository.deleteByUserId(userId);
        notificationRepository.deleteByFromUserOrToUser(userId);
        followRepository.deleteByFollowedOrFollower(userId);
        userRepository.deleteById(userId);

        List<Report> reports = reportRepository.findByReportedAndType(userId, "user");
        for (Report report : reports) {
            report.setStatus("resolved");
            reportRepository.save(report);
        }

        return ResponseEntity.ok(Map.of(
            "success", true,
            "message", "User deleted successfully"
        ));
    }

    public ResponseEntity<?> banUser(Long userId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal loggedUser = (UserPrincipal) auth.getPrincipal();
        User currentUser = loggedUser.getUser();

        if (!userRepository.existsById(userId)) {
            return ResponseEntity.ok(Map.of(
                "success", false,
                "message", "User not found"
            ));
        }

        if (!currentUser.getRole().equals("admin")) {
            return ResponseEntity.ok(Map.of(
                "success", false,
                "message", "Only admin can delete users"
            ));
        }

        User user = userRepository.findById(userId)
        .orElse(new User("deleted user"));

        if (user.getStatus().equals("active")) {
            user.setStatus("banned");
        } else if (user.getStatus().equals("banned")) {
            user.setStatus("active");
        }

        userRepository.save(user);

        List<Report> reports = reportRepository.findByReportedAndType(userId, "user");
        for (Report report : reports) {
            report.setStatus("resolved");
            reportRepository.save(report);
        }

        return ResponseEntity.ok(Map.of(
            "success", true,
            "message", "User banned/unbanned successfuly"
        ));
    }
}
