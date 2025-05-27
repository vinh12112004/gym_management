package com.example.gym.model;

import java.time.LocalDateTime;

public class WorkoutLog {
    private long id;
    private long userId;
    private String activity;
    private LocalDateTime timestamp;

    public WorkoutLog() {}
    public WorkoutLog(long id, long userId, String activity, LocalDateTime timestamp) {
        this.id = id;
        this.userId = userId;
        this.activity = activity;
        this.timestamp = timestamp;
    }
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }
    public String getActivity() { return activity; }
    public void setActivity(String activity) { this.activity = activity; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}