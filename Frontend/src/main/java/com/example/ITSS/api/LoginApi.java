package com.example.ITSS.api;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

public class LoginApi {
    public static LoginResult login(String email, String password) throws Exception {
        URL url = new URL("http://localhost:8080/auth/login");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        JSONObject json = new JSONObject();
        json.put("email", email);
        json.put("password", password);

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = json.toString().getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        int code = conn.getResponseCode();
        if (code == 200) {
            StringBuilder response = new StringBuilder();
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), "utf-8"))) {
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
            }
            JSONObject obj = new JSONObject(response.toString());
            String token = obj.optString("token");
            String name = obj.optString("name");
            Long id = obj.optLong("id");
            return new LoginResult(token, name, id);
        } else {
            StringBuilder response = new StringBuilder();
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getErrorStream(), "utf-8"))) {
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
            }
            throw new Exception("Đăng nhập thất bại: " + response.toString());
        }
    }

    public static class LoginResult {
        public final String token;
        public final String name;
        public final Long id;

        public LoginResult(String token, String name, Long id) {
            this.token = token;
            this.name = name;
            this.id = id;
        }
    }
}