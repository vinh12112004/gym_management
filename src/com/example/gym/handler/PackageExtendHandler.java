package com.example.gym.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.example.gym.service.PackageService;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class PackageExtendHandler implements HttpHandler {
    private final PackageService pkgService = new PackageService();
    private final Gson gson = new Gson();

    static class ExtendRequest { long id; int extraDays; }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"PUT".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }
        ExtendRequest req = gson.fromJson(
            new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8),
            ExtendRequest.class
        );
        boolean ok = pkgService.extendPackage(req.id, req.extraDays);
        exchange.sendResponseHeaders(ok ? 200 : 404, -1);
    }
}