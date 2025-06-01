package com.example.ITSS.controller;

import com.example.ITSS.model.Role;
import com.example.ITSS.model.User;
import com.example.ITSS.repository.RoleRepository;
import com.example.ITSS.repository.UserRepository;
import com.example.ITSS.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private com.example.ITSS.security.CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> user) {
        try {
            String email = user.get("email");
            String password = user.get("password");
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            String token = jwtUtil.generateToken(userDetails.getUsername());

            // Lấy tên người dùng từ entity User
            User dbUser = userRepository.findByEmail(email).orElse(null);
            Long id = dbUser != null ? dbUser.getId() : null;
            String name = dbUser != null ? dbUser.getName() : "";

            return ResponseEntity.ok(Map.of(
                "token", token,
                "id", id,
                "name", name
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Login failed: " + e.getMessage());
        }
    }

    // Optional: Đăng ký tài khoản nhanh (mặc định role là ROLE_USER)
    @PostMapping("/register")
    public Map<String, String> register(@RequestBody User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Role roleUser = roleRepository.findByName("ROLE_USER")
                .orElseGet(() -> roleRepository.save(new Role(null, "ROLE_USER")));
        user.setRoles(Set.of(roleUser));
        userRepository.save(user);
        return Map.of("message", "User registered successfully");
    }
}