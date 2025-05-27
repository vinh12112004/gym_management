package com.example.gym.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.example.gym.service.PackageService;

import java.io.IOException;

public class PackageDeleteHandler implements HttpHandler {
    private final PackageService pkgService = new PackageService();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"DELETE".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }
        String query = exchange.getRequestURI().getQuery();
        long id = 0;
        if (query != null && query.startsWith("id=")) {
            id = Long.parseLong(query.substring(3));
        }
        boolean ok = pkgService.deletePackage(id);
        exchange.sendResponseHeaders(ok ? 204 : 404, -1);
    }
}