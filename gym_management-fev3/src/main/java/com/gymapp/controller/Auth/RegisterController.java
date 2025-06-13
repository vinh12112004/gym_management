package com.gymapp.controller.Auth;

import com.gymapp.api.ApiClient;
import com.gymapp.api.ApiConfig;
import com.gymapp.model.User;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.http.HttpResponse;

public class RegisterController {
    
    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Button registerButton;
    @FXML private Button backButton;
    @FXML private ProgressIndicator progressIndicator;
    
    @FXML
    private void handleRegister() {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        
        if (!validateInput(username, email, password, confirmPassword)) {
            return;
        }
        
        setLoading(true);
        
        Task<Void> registerTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                User newUser = new User(username, email, password);
                HttpResponse<String> response = ApiClient.getInstance().post(ApiConfig.AUTH_REGISTER, newUser);
                
                if (response.statusCode() == 201 || response.statusCode() == 200) {
                    Platform.runLater(() -> {
                        showAlert("Success", "Registration successful! Please login.", Alert.AlertType.INFORMATION);
                        handleBack();
                    });
                } else {
                    Platform.runLater(() -> {
                        showAlert("Registration Failed", "Could not create account. Please try again.", Alert.AlertType.ERROR);
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
                    showAlert("Error", "Connection failed. Please try again.", Alert.AlertType.ERROR);
                });
            }
        };
        
        new Thread(registerTask).start();
    }
    
    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Auth/Login.fxml"));
            Scene scene = new Scene(loader.load(), 400, 300);
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Could not load login form.", Alert.AlertType.ERROR);
        }
    }
    
    private boolean validateInput(String username, String email, String password, String confirmPassword) {
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showAlert("Validation Error", "All fields are required.", Alert.AlertType.WARNING);
            return false;
        }
        
        if (!email.contains("@") || !email.contains(".")) {
            showAlert("Validation Error", "Please enter a valid email address.", Alert.AlertType.WARNING);
            return false;
        }
        
        if (password.length() < 6) {
            showAlert("Validation Error", "Password must be at least 6 characters long.", Alert.AlertType.WARNING);
            return false;
        }
        
        if (!password.equals(confirmPassword)) {
            showAlert("Validation Error", "Passwords do not match.", Alert.AlertType.WARNING);
            return false;
        }
        
        return true;
    }
    
    private void setLoading(boolean loading) {
        Platform.runLater(() -> {
            progressIndicator.setVisible(loading);
            registerButton.setDisable(loading);
            backButton.setDisable(loading);
        });
    }
    
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
