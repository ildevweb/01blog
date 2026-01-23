package com.example.app.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.List;

import com.example.app.dto.UserInfos;
import com.example.app.entity.User;
import com.example.app.repository.UserRepository;
import com.example.app.security.UserPrincipal;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final FollowService followService;

    public UserService(UserRepository userRepository, FollowService followService) {
        this.userRepository = userRepository;
        this.followService = followService;
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
                    0,
                    0,
                    false
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

}
