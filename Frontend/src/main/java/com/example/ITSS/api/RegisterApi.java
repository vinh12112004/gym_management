package com.example.ITSS.api;

import java.io.*;
import java.net.*;
import org.json.JSONObject;

public class RegisterApi {
    public static void register(String fullName, String username, String email, String password) throws Exception {
        URL url = new URL("http://localhost:8080/auth/register");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        JSONObject json = new JSONObject();
        json.put("name", fullName);
        json.put("username", username);
        json.put("email", email);
        json.put("password", password);

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = json.toString().getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        int code = conn.getResponseCode();
        if (code == 200 || code == 201) {
            // Đăng ký thành công
        } else {
            // Xử lý lỗi
        }
    }
}