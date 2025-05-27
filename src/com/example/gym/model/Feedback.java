package com.example.gym.model;

public class Feedback {
    private long id;
    private long userId;
    private String message;

    public Feedback() {}
    public Feedback(long id, long userId, String message) {
        this.id = id;
        this.userId = userId;
        this.message = message;
    }
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}