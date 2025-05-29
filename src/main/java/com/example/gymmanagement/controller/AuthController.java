package com.example.gymmanagement.controller;

import com.example.gymmanagement.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService auth;

    public AuthController(AuthService auth) {
        this.auth = auth;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody AuthRequest req) {
        String msg = auth.register(req.getUsername(), req.getPassword());
        return ResponseEntity.ok(msg);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest req) {
        String token = auth.login(req.getUsername(), req.getPassword());
        return ResponseEntity.ok(new AuthResponse(token));
    }

    // DTO classes không dùng Lombok
    public static class AuthRequest {
        private String username;
        private String password;

        public AuthRequest() {}

        public String getUsername() {
            return username;
        }
        public void setUsername(String username) {
            this.username = username;
        }
        public String getPassword() {
            return password;
        }
        public void setPassword(String password) {
            this.password = password;
        }
    }

    public static class AuthResponse {
        private String token;
        public AuthResponse() {}
        public AuthResponse(String token) {
            this.token = token;
        }
        public String getToken() {
            return token;
        }
        public void setToken(String token) {
            this.token = token;
        }
    }
}
