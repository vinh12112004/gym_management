package com.example.gym.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.example.gym.service.WorkoutLogService;
import com.example.gym.model.WorkoutLog;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class WorkoutLogHandler implements HttpHandler {
    private final WorkoutLogService logSvc = new WorkoutLogService();
    private final Gson gson = new Gson();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            WorkoutLog log = gson.fromJson(
                new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8),
                WorkoutLog.class
            );
            WorkoutLog created = logSvc.create(log);
            byte[] resp = gson.toJson(created).getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type","application/json");
            exchange.sendResponseHeaders(201, resp.length);
            try (var os = exchange.getResponseBody()) { os.write(resp); }
        } else {
            exchange.sendResponseHeaders(405, -1);
        }
    }
}