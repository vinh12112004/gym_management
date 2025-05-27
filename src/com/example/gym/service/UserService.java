package com.example.gym.service;

import com.example.gym.model.User;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class UserService {
    private final ConcurrentHashMap<String, User> users = new ConcurrentHashMap<>();
    private final AtomicLong seq = new AtomicLong(1);

    public User register(String email, String password, String fullName) {
        if (users.containsKey(email)) return null;
        User u = new User(seq.getAndIncrement(), email, password, fullName);
        users.put(email, u);
        return u;
    }

    public boolean updateProfile(long userId, String newName) {
        for (User u : users.values()) {
            if (u.getId() == userId) {
                u.setFullName(newName);
                return true;
            }
        }
        return false;
    }
}