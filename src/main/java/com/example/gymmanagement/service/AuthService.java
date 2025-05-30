package com.example.gymmanagement.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.gymmanagement.model.User;
import com.example.gymmanagement.repository.UserRepository;
import com.example.gymmanagement.util.JwtUtil;

@Service
public class AuthService {

    private final UserRepository userRepo;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepo,
                       PasswordEncoder encoder,
                       AuthenticationManager authManager,
                       JwtUtil jwtUtil) {
        this.userRepo = userRepo;
        this.encoder = encoder;
        this.authManager = authManager;
        this.jwtUtil = jwtUtil;
    }

    public String register(String username, String rawPassword) {
        if (userRepo.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username already taken");
        }
        User u = new User(username, encoder.encode(rawPassword), "ROLE_USER");
        userRepo.save(u);
        return "Register successful";
    }

    public String login(String username, String rawPassword) {
        // Xác thực username/password
        authManager.authenticate(
            new UsernamePasswordAuthenticationToken(username, rawPassword)
        );
        // Lấy User entity từ DB
        User u = userRepo.findByUsername(username)
                         .orElseThrow(() -> new IllegalArgumentException("User not found"));
        // Tạo UserDetails để sinh JWT
        UserDetails ud = org.springframework.security.core.userdetails.User
            .withUsername(u.getUsername())
            .password(u.getPassword())
            .authorities(u.getRoles().split(","))
            .build();
        // Trả về token
        return jwtUtil.generateToken(ud);
    }
}
