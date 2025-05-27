package com.example.gym.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.example.gym.service.EmployeeService;
import com.example.gym.model.Employee;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class EmployeeHandler implements HttpHandler {
    private final EmployeeService empService = new EmployeeService();
    private final Gson gson = new Gson();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        switch (method) {
            case "GET":
                List<Employee> list = empService.listAll();
                String json = gson.toJson(list);
                byte[] data = json.getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().add("Content-Type","application/json");
                exchange.sendResponseHeaders(200, data.length);
                try (OutputStream os = exchange.getResponseBody()) { os.write(data); }
                break;
            case "POST":
                Employee e = gson.fromJson(
                    new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8),
                    Employee.class
                );
                Employee created = empService.create(e);
                byte[] resp = gson.toJson(created).getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().add("Content-Type","application/json");
                exchange.sendResponseHeaders(201, resp.length);
                try (OutputStream os = exchange.getResponseBody()) { os.write(resp); }
                break;
            case "PUT":
                Employee upd = gson.fromJson(
                    new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8),
                    Employee.class
                );
                boolean updated = empService.update(upd);
                exchange.sendResponseHeaders(updated ? 200 : 404, -1);
                break;
            case "DELETE":
                String query = exchange.getRequestURI().getQuery();
                long id = query != null && query.startsWith("id=") ? Long.parseLong(query.substring(3)) : 0;
                boolean deleted = empService.delete(id);
                exchange.sendResponseHeaders(deleted ? 204 : 404, -1);
                break;
            default:
                exchange.sendResponseHeaders(405, -1);
        }
    }
}