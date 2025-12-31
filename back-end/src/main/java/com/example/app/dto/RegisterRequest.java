package com.example.app.dto;

import java.util.regex.Pattern;

public class RegisterRequest {

    private String username;
    private String email;
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        if (!validateUsername(username)) {
            throw new RuntimeException("Invalid username");
        }
        this.username = username;
    }

    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        if (!validateEmail(email)) {
            throw new RuntimeException("Invalid email");
        }
        this.email = email;
    }

    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        if (!validatePassword(password)) {
            throw new RuntimeException("Invalid password");
        }
        this.password = password;
    }

    // 1️⃣ Validate email
    public static boolean validateEmail(String email) {
        // Simple regex for most emails
        String emailRegex = "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$";
        return Pattern.matches(emailRegex, email);
    }

    // ------------------------
    // 2️⃣ Validate username
    public static boolean validateUsername(String username) {
        // Letters, numbers, underscore, 3-20 chars
        String usernameRegex = "^[a-zA-Z0-9_]{3,20}$";
        return Pattern.matches(usernameRegex, username);
    }

    // ------------------------
    // 3️⃣ Validate password
    public static boolean validatePassword(String password) {
        // Minimum 8 chars, at least 1 letter and 1 number
        String passwordRegex = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$";
        return Pattern.matches(passwordRegex, password);
    }
}
