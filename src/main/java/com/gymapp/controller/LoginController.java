package com.gymapp.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.gymapp.api.ApiClient;
import com.gymapp.api.ApiConfig;
import com.gymapp.model.User;
import com.gymapp.util.SessionManager;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.http.HttpResponse;

public class LoginController {
    
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Button registerButton;
    @FXML private ProgressIndicator progressIndicator;
    
    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        
        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Please enter both username and password.");
            return;
        }
        
        setLoading(true);
        
        Task<Void> loginTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                User loginUser = new User(username, "", password);
                HttpResponse<String> response = ApiClient.getInstance().post(ApiConfig.AUTH_LOGIN, loginUser);
                
                if (response.statusCode() == 200) {
                    JsonNode jsonResponse = ApiClient.getInstance().getObjectMapper().readTree(response.body());
                    String token = jsonResponse.get("token").asText();
                    
                    Platform.runLater(() -> {
                        SessionManager.getInstance().login(token, username);
                        openMainWindow();
                    });
                } else {
                    Platform.runLater(() -> {
                        showAlert("Login Failed", "Invalid username or password.");
                    });
                }
                return null;
            }
            
            @Override
            protected void succeeded() {
                setLoading(false);
            }
            
            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    setLoading(false);
                    showAlert("Error", "Connection failed. Please try again.");
                });
            }
        };
        
        new Thread(loginTask).start();
    }
    
    @FXML
    private void handleRegister() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Register.fxml"));
            Scene scene = new Scene(loader.load(), 400, 350);
            Stage stage = (Stage) registerButton.getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Could not load registration form.");
        }
    }
    
    private void openMainWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainWindow.fxml"));
            Scene scene = new Scene(loader.load(), 1200, 800);
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setMaximized(true);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Could not load main window.");
        }
    }
    
    private void setLoading(boolean loading) {
        Platform.runLater(() -> {
            progressIndicator.setVisible(loading);
            loginButton.setDisable(loading);
            registerButton.setDisable(loading);
        });
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
