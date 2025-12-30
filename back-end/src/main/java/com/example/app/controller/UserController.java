package com.example.app.controller;

import org.springframework.web.bind.annotation.*;

import com.example.app.entity.User;
import com.example.app.service.UserService;
import com.example.app.dto.RegisterRequest;


@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public User register(@RequestBody RegisterRequest request) {

        return userService.register(
            request.getUsername(),
            request.getEmail(),
            request.getPassword()
        );
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }
}
