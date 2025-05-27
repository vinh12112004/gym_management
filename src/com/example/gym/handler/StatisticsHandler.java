package com.example.gym.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.example.gym.service.StatisticsService;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class StatisticsHandler implements HttpHandler {
    private final StatisticsService statsSvc = new StatisticsService();
    private final Gson gson = new Gson();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }
        Map<String,Object> data = statsSvc.getOverview();
        byte[] resp = gson.toJson(data).getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type","application/json");
        exchange.sendResponseHeaders(200, resp.length);
        try (OutputStream os = exchange.getResponseBody()) { os.write(resp); }
    }
}