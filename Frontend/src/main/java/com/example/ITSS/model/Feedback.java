package com.example.ITSS.model;

public class Feedback {
    private int id;
    private int userId;
    private String targetType;
    private int targetId;
    private String content;
    private int rating;
    private String createdAt;

    public Feedback(int id, int userId, String targetType, int targetId, String content, int rating, String createdAt) {
        this.id = id;
        this.userId = userId;
        this.targetType = targetType;
        this.targetId = targetId;
        this.content = content;
        this.rating = rating;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public int getUserId() { return userId; }
    public String getTargetType() { return targetType; }
    public int getTargetId() { return targetId; }
    public String getContent() { return content; }
    public int getRating() { return rating; }
    public String getCreatedAt() { return createdAt; }

    public void setId(int id) { this.id = id; }
    public void setUserId(int userId) { this.userId = userId; }
    public void setTargetType(String targetType) { this.targetType = targetType; }
    public void setTargetId(int targetId) { this.targetId = targetId; }
    public void setContent(String content) { this.content = content; }
    public void setRating(int rating) { this.rating = rating; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}