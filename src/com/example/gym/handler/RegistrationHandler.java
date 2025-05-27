package com.example.gym.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.example.gym.service.UserService;
import com.example.gym.model.User;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class RegistrationHandler implements HttpHandler {
    private final UserService userService = new UserService();
    private final Gson gson = new Gson();

    static class RegisterRequest { String email; String password; String fullName; }
    static class RegisterResponse { String status; long userId; }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }
        RegisterRequest req = gson.fromJson(
            new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8),
            RegisterRequest.class
        );
        User u = userService.register(req.email, req.password, req.fullName);
        RegisterResponse resp = new RegisterResponse();
        if (u != null) {
            resp.status = "ok";
            resp.userId = u.getId();
            byte[] json = gson.toJson(resp).getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(201, json.length);
            try (OutputStream os = exchange.getResponseBody()) { os.write(json); }
        } else {
            resp.status = "fail";
            byte[] json = gson.toJson(resp).getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(400, json.length);
            try (OutputStream os = exchange.getResponseBody()) { os.write(json); }
        }
    }
}