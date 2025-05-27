package com.example.gym.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.example.gym.service.PackageService;
import com.example.gym.model.Package;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class PackageHandler implements HttpHandler {
    private final PackageService pkgService = new PackageService();
    private final Gson gson = new Gson();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();

        if ("GET".equalsIgnoreCase(method)) {
            // UC014: Trả danh sách tất cả các gói tập
            List<Package> all = pkgService.listAll();
            String json = gson.toJson(all);
            byte[] resp = json.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type","application/json");
            exchange.sendResponseHeaders(200, resp.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(resp);
            }
        } else if ("POST".equalsIgnoreCase(method)) {
            // UC003: Tạo gói tập mới
            Package pkg = gson.fromJson(
                new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8),
                Package.class
            );
            Package created = pkgService.create(pkg);
            String json = gson.toJson(created);
            byte[] resp = json.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type","application/json");
            exchange.sendResponseHeaders(201, resp.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(resp);
            }
        } else {
            // Các method không hỗ trợ
            exchange.sendResponseHeaders(405, -1);
        }
    }