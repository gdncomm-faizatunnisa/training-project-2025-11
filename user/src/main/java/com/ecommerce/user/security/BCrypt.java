package com.ecommerce.user.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class BCrypt {

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // Hash the password
    public static String hash(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    // Validate raw password with stored hashed password
    public static boolean verify(String rawPassword, String hashedPassword) {
        return passwordEncoder.matches(rawPassword, hashedPassword);
    }
}

