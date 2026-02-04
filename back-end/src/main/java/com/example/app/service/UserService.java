package com.example.app.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import com.example.app.dto.UserInfos;
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
    
    public List<UserInfos> getAllUsers() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userp = (UserPrincipal) auth.getPrincipal();
        Long userId = userp.getId();
        User user = userp.getUser();

        String role = user.getRole();

        List<User> users;
        if (role.equals("admin")) {
            users = userRepository.findByIdNot(userId);
        } else {
            users = userRepository.findByIdNotAndStatus(userId, "active");
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
            .orElseThrow(() -> new RuntimeException("User not found"));
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


    public void deleteUser(Long userId) {

        if (!userRepository.existsById(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userp = (UserPrincipal) auth.getPrincipal();
        User user = userp.getUser();

        if (!user.getRole().equals("admin")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        postLikeRepository.deleteByUserId(userId);
        notificationRepository.deleteByFromUserOrToUser(userId);
        followRepository.deleteByFollowedOrFollower(userId);
        userRepository.deleteById(userId);

        reportRepository.findByReportedAndType(userId, "user")
        .ifPresent(report -> {
            report.setStatus("resolved");
            reportRepository.save(report);
        });
    }

    public void banUser(Long userId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal loggedUser = (UserPrincipal) auth.getPrincipal();
        User currentUser = loggedUser.getUser();

        if (!userRepository.existsById(userId) || !currentUser.getRole().equals("admin")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        User user = userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getStatus().equals("active")) {
            user.setStatus("banned");
        } else if (user.getStatus().equals("banned")) {
            user.setStatus("active");
        }

        userRepository.save(user);

        reportRepository.findByReportedAndType(userId, "user")
        .ifPresent(report -> {
            report.setStatus("resolved");
            reportRepository.save(report);
        });
    }
}
