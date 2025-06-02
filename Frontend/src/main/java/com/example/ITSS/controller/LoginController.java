package com.example.ITSS.controller;

import com.example.ITSS.UserSession;
import com.example.ITSS.api.LoginApi;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class LoginController {
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;

    @FXML
    private void handleLogin(ActionEvent event) {
        String email = emailField.getText();
        String password = passwordField.getText();

        try {
            LoginApi.LoginResult result = LoginApi.login(email, password);
            UserSession.setToken(result.token);
            UserSession.setUsername(result.name);
            UserSession.setId(result.id);
            System.out.println("Token: " + UserSession.getToken());
            goToDashboard(result.name);
        } catch (Exception e) {
            showAlert("Đăng nhập thất bại: " + e.getMessage());
        }
    }

    private void goToDashboard(String name) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Dashboard.fxml"));
            Parent root = loader.load();
            DashboardController dashboardController = loader.getController();
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Dashboard");
        } catch (Exception e) {
            showAlert("Không thể chuyển sang Dashboard: " + e.getMessage());
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.showAndWait();
    }
}