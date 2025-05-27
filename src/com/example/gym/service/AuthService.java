package com.example.gym.service;

import com.example.gym.model.User;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class AuthService {
    private final ConcurrentHashMap<String, User> users = new ConcurrentHashMap<>();
    private final AtomicLong seq = new AtomicLong(1);

    public AuthService() {
        // Demo account
        User admin = new User(seq.getAndIncrement(), "admin@gym.com", "secret", "Administrator");
        users.put(admin.getEmail(), admin);
    }

    public User login(String email, String password) {
        User u = users.get(email);
        if (u != null && u.getPassword().equals(password)) {
            return u;
        }
        return null;
    }
}