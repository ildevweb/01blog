package com.example.app.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.example.app.entity.User;
import com.example.app.service.UserService;


@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 1️⃣ GET /api/users → get all users
    @GetMapping
    public List<User> getUsers() {
        return userService.getAllUsers();
    }

    // 2️⃣ GET /api/users/{id} → get single user by ID
    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    // 3️⃣ POST /api/users → create a new user
    @PostMapping
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    // 4️⃣ PUT /api/users/{id} → update existing user
    @PutMapping("/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody User user) {
        return userService.updateUser(id, user);
    }

    // 5️⃣ DELETE /api/users/{id} → delete user by ID
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}
