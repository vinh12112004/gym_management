package com.example.gym.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;

public class NotFoundHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange ex) throws IOException {
        String resp = "404 Not Found";
        ex.sendResponseHeaders(404, resp.length());
        try (var os = ex.getResponseBody()) {
            os.write(resp.getBytes());
        }
    }
}