package com.example.gym.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.example.gym.service.UserService;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class ProfileUpdateHandler implements HttpHandler {
    private final UserService userService = new UserService();
    private final Gson gson = new Gson();

    static class ProfileRequest { long userId; String fullName; }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"PUT".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }
        ProfileRequest req = gson.fromJson(
            new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8),
            ProfileRequest.class
        );
        boolean ok = userService.updateProfile(req.userId, req.fullName);
        exchange.sendResponseHeaders(ok ? 200 : 404, -1);
    }
}