package com.gymapp.util;

import javafx.stage.Stage;

public class SessionManager {
    private static SessionManager instance;
    private String authToken;
    private String currentUser;
    private Stage primaryStage;
    
    private SessionManager() {}
    
    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }
    
    public void login(String token, String username) {
        this.authToken = token;
        this.currentUser = username;
    }
    
    public void logout() {
        this.authToken = null;
        this.currentUser = null;
    }
    
    public boolean isLoggedIn() {
        return authToken != null && !authToken.isEmpty();
    }
    
    public String getAuthToken() {
        return authToken;
    }
    
    public String getCurrentUser() {
        return currentUser;
    }
    
    public Stage getPrimaryStage() {
        return primaryStage;
    }
    
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
}
