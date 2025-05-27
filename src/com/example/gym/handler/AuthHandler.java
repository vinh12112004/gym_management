package com.example.gym.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.example.gym.service.AuthService;
import com.example.gym.model.User;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class AuthHandler implements HttpHandler {
    private final AuthService authService = new AuthService();
    private final Gson gson = new Gson();

    static class LoginRequest { String email; String password; }
    static class LoginResponse { String status; long userId; }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }
        LoginRequest req = gson.fromJson(
            new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8),
            LoginRequest.class
        );
        User user = authService.login(req.email, req.password);
        LoginResponse resp = new LoginResponse();
        if (user != null) {
            resp.status = "ok";
            resp.userId = user.getId();
            byte[] json = gson.toJson(resp).getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, json.length);
            try (OutputStream os = exchange.getResponseBody()) { os.write(json); }
        } else {
            resp.status = "fail";
            byte[] json = gson.toJson(resp).getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(401, json.length);
            try (OutputStream os = exchange.getResponseBody()) { os.write(json); }
        }
    }
}