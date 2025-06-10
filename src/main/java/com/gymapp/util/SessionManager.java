package com.gymapp.util;

import javafx.stage.Stage;

public class SessionManager {
    private static SessionManager instance;
    private String authToken;
    private String currentUser;
    private String currentRole;
    private Stage primaryStage;

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void login(String token, String username, String role) {
        this.authToken = token;
        this.currentUser = username;
        this.currentRole = role;
    }

    public void logout() {
        this.authToken = null;
        this.currentUser = null;
        this.currentRole = null;
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

    public String getCurrentRole() {
        return currentRole;
    }

    public void setCurrentRole(String role) {
        this.currentRole = role;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
}