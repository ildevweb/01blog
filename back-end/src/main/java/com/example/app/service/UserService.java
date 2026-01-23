package com.example.app.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.List;

import com.example.app.dto.UserInfos;
import com.example.app.entity.User;
import com.example.app.repository.FollowRepository;
import com.example.app.repository.UserRepository;
import com.example.app.security.UserPrincipal;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final FollowService followService;
    private final FollowRepository followRepository;

    public UserService(UserRepository userRepository, FollowService followService, FollowRepository followRepository) {
        this.userRepository = userRepository;
        this.followService = followService;
        this.followRepository = followRepository;
    }
    
    public List<UserInfos> getAllUsers() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal user = (UserPrincipal) auth.getPrincipal();
        Long userId = user.getId();

        return userRepository.findByIdNot(userId)
            .stream()
            .map(usr -> {

                return new UserInfos(
                    usr.getId(),
                    usr.getName(),
                    followService.countFollowers(usr.getId()),
                    followService.countFolloweds(usr.getId()),
                    followService.isFollowing(userId, usr.getId())
                );
            })
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
                followService.countFollowers(user.getId()),
                followService.countFolloweds(user.getId()),
                followService.isFollowing(currentUser.getId(), user.getId())
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
                    followService.countFollowers(usr.getId()),
                    followService.countFolloweds(usr.getId()),
                    followService.isFollowing(user.getId(), usr.getId())
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
                    followService.countFollowers(usr.getId()),
                    followService.countFolloweds(usr.getId()),
                    followService.isFollowing(user.getId(), usr.getId())
                );
            })
            .toList();
    }

}
