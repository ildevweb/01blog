package com.example.app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import com.example.app.dto.LikeRequest;
import com.example.app.dto.UserInfos;
import com.example.app.entity.User;
import com.example.app.security.UserPrincipal;
import com.example.app.service.UserService;
import com.example.app.service.FollowService;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {
    private final UserService userService;
    private final FollowService followService;

    public UserController(UserService userService, FollowService followService) {
        this.userService = userService;
        this.followService = followService;
    }
    
    @GetMapping("/all")
    public ResponseEntity<List<UserInfos>> getUsers() {
        List<UserInfos> allUsers = userService.getAllUsers();
        return ResponseEntity.ok(allUsers);
    }

    @GetMapping("/profile/me")
    public ResponseEntity<UserInfos> getMyProfile() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal user = (UserPrincipal) auth.getPrincipal();

        return ResponseEntity.ok(userService.getProfile(user.getId()));
    }

    @GetMapping("/profile/{id}")
    public ResponseEntity<UserInfos> getProfile(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getProfile(id));
    }

    @PostMapping("/follow")
    public ResponseEntity<?> toggleLike( @RequestBody LikeRequest request) {
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal user = (UserPrincipal) auth.getPrincipal();
        User currentUser = user.getUser();
        followService.follow(currentUser.getId(), request.getUserId());

        return ResponseEntity.ok("Followed Successfully");
    }
}
