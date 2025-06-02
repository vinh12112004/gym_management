package com.example.ITSS.api;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.example.ITSS.UserSession;
import com.example.ITSS.model.Feedback;

public class FeedbackApi {
    public static List<Feedback> getAllFeedbacks() throws Exception {
        URL url = new URL("http://localhost:8080/api/feedback");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");
        // Thêm token vào header
        conn.setRequestProperty("Authorization", "Bearer " + UserSession.getToken());

        int code = conn.getResponseCode();
        if (code != 200) throw new Exception("Failed to get feedbacks");

        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) response.append(line);
        br.close();

        JSONArray arr = new JSONArray(response.toString());
        List<Feedback> list = new ArrayList<>();
        for (int i = 0; i < arr.length(); i++) {
            JSONObject obj = arr.getJSONObject(i);
            Feedback fb = new Feedback(
                    obj.optInt("id"),
                    obj.optInt("userId"),
                    obj.optString("targetType"),
                    obj.optInt("targetId"),
                    obj.optString("content"),
                    obj.optInt("rating"),
                    obj.optString("createdAt")
            );
            list.add(fb);
        }
        return list;
    }

    public static void sendFeedback(Feedback fb) throws Exception {
        URL url = new URL("http://localhost:8080/api/feedback");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        // Thêm token vào header
        conn.setRequestProperty("Authorization", "Bearer " + UserSession.getToken());
        conn.setDoOutput(true);

        JSONObject json = new JSONObject();
        json.put("userId", fb.getUserId());
        json.put("targetType", fb.getTargetType());
        json.put("targetId", fb.getTargetId());
        json.put("content", fb.getContent());
        json.put("rating", fb.getRating());

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = json.toString().getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        int code = conn.getResponseCode();
        if (code != 200 && code != 201) throw new Exception("Send feedback failed");
    }
}