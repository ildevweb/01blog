package com.example.app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.app.entity.User;
import com.example.app.service.AuthService;
import com.example.app.dto.RegisterRequest;
import com.example.app.dto.LoginRequest;
import com.example.app.dto.LoginResponse;


@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }


    @PostMapping("/register")
    public User register(@RequestBody RegisterRequest request) {

        return authService.register(
            request.getUsername(),
            request.getEmail(),
            request.getPassword()
        );
    }


    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return authService.login(request.getEmail(), request.getPassword());
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return authService.getUserById(id);
    }
}
