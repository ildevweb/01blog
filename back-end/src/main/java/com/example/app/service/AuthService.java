package com.example.app.service;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.app.dto.LoginResponse;
import com.example.app.entity.User;
import com.example.app.repository.UserRepository;
import com.example.app.security.JwtUtil;

import java.util.Map;
import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public ResponseEntity<?> register(String username, String email, String password) {

        if (userRepository.existsByEmail(email)) {
            return ResponseEntity.ok(
                Map.of(
                    "success", false,
                    "message", "Email already exist"
                )
            );
        }

        if (userRepository.existsByName(username)) {
            return ResponseEntity.ok(
                Map.of(
                    "success", false,
                    "message", "Username already exist"
                )
            );
        }

        boolean isEmpty = !userRepository.existsBy();

        User user = new User();
        user.setName(username);
        user.setEmail(email);
        if (isEmpty) {
            user.setRole("admin");
        } else {
            user.setRole("user");
        }
        user.setStatus("active");
        
        String hashedPassword = passwordEncoder.encode(password);
        user.setPassword(hashedPassword);

        userRepository.save(user);

        return ResponseEntity.ok(
            Map.of(
                "success", true,
                "message", "Registred succesfully"
            )
        );
    }

    public ResponseEntity<?> login(String email, String password) {

        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            return ResponseEntity.ok(
                Map.of(
                    "success", false,
                    "message", "Invalid email"
                )
            );
        }

        User user = optionalUser.get();

        if (!passwordEncoder.matches(password, user.getPassword())) {
            return ResponseEntity.ok(
                Map.of(
                    "success", false,
                    "message", "Invalid password"
                )
            );
        }

        if (user.getStatus().equals("banned")) {
            return ResponseEntity.ok(
                Map.of(
                    "success", false,
                    "message", "Banned user"
                )
            );
        }

        String token = JwtUtil.generateToken(
            user.getId(),
            user.getEmail(),
            user.getRole()
        );

        //return ResponseEntity.ok(new LoginResponse(token));
        return ResponseEntity.ok(
            Map.of(
                "success", true,
                "value", new LoginResponse(token)
            )
        );
    }

}

