package com.example.app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import com.example.app.dto.UserInfos;
import com.example.app.service.UserService;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    @GetMapping("/all")
    public ResponseEntity<List<UserInfos>> getUsers() {
        List<UserInfos> allUsers = userService.getAllUsers();
        return ResponseEntity.ok(allUsers);
    }
}
