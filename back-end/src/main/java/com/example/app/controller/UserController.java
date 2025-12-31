package com.example.app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.app.entity.User;
import com.example.app.security.JwtUtil;
import com.example.app.service.UserService;
import com.example.app.dto.RegisterRequest;
import com.example.app.dto.LoginRequest;
import com.example.app.dto.LoginResponse;
import com.example.app.repository.UserRepository;


@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    public UserController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }


    @PostMapping("/register")
    public User register(@RequestBody RegisterRequest request) {

        return userService.register(
            request.getUsername(),
            request.getEmail(),
            request.getPassword()
        );
    }

    /*@PostMapping("/login")
    public User login(@RequestBody LoginRequest request) {

        return userService.login(
            request.getEmail(),
            request.getPassword()
        );
    }*/

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest request
    ) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!user.getPassword().equals(request.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        String token = JwtUtil.generateToken(
                user.getId(),
                user.getEmail()
        );

        return ResponseEntity.ok(new LoginResponse(token));
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }
}
