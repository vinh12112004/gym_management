package com.example.ITSS.controller;

import com.example.ITSS.UserSession;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class DashboardController {
    @FXML
    private Label lblUsername;

    @FXML
    public void initialize() {
        lblUsername.setText(UserSession.getUsername());
    }

    @FXML
    private void showFeedbacks() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Feedbacks.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) lblUsername.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Feedbacks");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}