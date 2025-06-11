package com.gymapp.controller;

import com.gymapp.util.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    
    @FXML private Label userLabel;
    @FXML private Button logoutButton;
    @FXML private StackPane contentArea;
    @FXML private Button dashboardBtn;
    @FXML private Button equipmentBtn;
    @FXML private Button membersBtn;
    @FXML private Button packagesBtn;
    @FXML private Button roomsBtn;
    @FXML private Button sessionsBtn;
    @FXML private Button feedbackBtn;
    @FXML private Button usersBtn;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        userLabel.setText("Welcome, " + SessionManager.getInstance().getCurrentUser());
        String role = SessionManager.getInstance().getCurrentRole();
        if ("TRAINER".equalsIgnoreCase(role)) {
            feedbackBtn.setVisible(false);
            feedbackBtn.setManaged(false); // Ẩn hoàn toàn khỏi layout
            usersBtn.setVisible(false);
            usersBtn.setManaged(false); // Ẩn hoàn toàn khỏi layout
        }
        if("MEMBER".equalsIgnoreCase(role)) {
            equipmentBtn.setVisible(false);
            equipmentBtn.setManaged(false); // Ẩn hoàn toàn khỏi layout
            membersBtn.setVisible(false);
            membersBtn.setManaged(false); // Ẩn hoàn toàn khỏi layout
            packagesBtn.setVisible(false);
            packagesBtn.setManaged(false); // Ẩn hoàn toàn khỏi layout
            roomsBtn.setVisible(false);
            roomsBtn.setManaged(false); // Ẩn hoàn toàn khỏi layout
            usersBtn.setVisible(false);
            usersBtn.setManaged(false); // Ẩn hoàn toàn khỏi layout
        }
        showDashboard(); // Show dashboard by default
    }
    
    @FXML
    private void handleLogout() {
        SessionManager.getInstance().logout();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
            Scene scene = new Scene(loader.load(), 400, 300);
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setResizable(false);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Could not load login form.");
        }
    }
    
    @FXML
    private void showDashboard() {
        loadContent("/fxml/Dashboard.fxml");
        setActiveButton(dashboardBtn);
    }
    
    @FXML
    private void showEquipment() {
        loadContent("/fxml/Equipment.fxml");
        setActiveButton(equipmentBtn);
    }
    
    @FXML
    private void showMembers() {
        loadContent("/fxml/Members.fxml");
        setActiveButton(membersBtn);
    }
    
    @FXML
    private void showPackages() {
        loadContent("/fxml/Packages.fxml");
        setActiveButton(packagesBtn);
    }
    
    @FXML
    private void showRooms() {
        loadContent("/fxml/Rooms.fxml");
        setActiveButton(roomsBtn);
    }
    
    @FXML
    private void showSessions() {
        loadContent("/fxml/WorkoutSessions.fxml");
        setActiveButton(sessionsBtn);
    }
    
    @FXML
    private void showFeedback() {
        loadContent("/fxml/Feedback.fxml");
        setActiveButton(feedbackBtn);
    }

    @FXML
    private void showUsers() {
        loadContent("/fxml/Users.fxml");
        setActiveButton(usersBtn);
    }
    
    private void loadContent(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node content = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(content);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Could not load " + fxmlPath);
        }
    }

    
    
    private void setActiveButton(Button activeButton) {
        // Reset all button styles
        dashboardBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-alignment: center-left; -fx-padding: 15;");
        equipmentBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-alignment: center-left; -fx-padding: 15;");
        membersBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-alignment: center-left; -fx-padding: 15;");
        packagesBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-alignment: center-left; -fx-padding: 15;");
        roomsBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-alignment: center-left; -fx-padding: 15;");
        sessionsBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-alignment: center-left; -fx-padding: 15;");
        feedbackBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-alignment: center-left; -fx-padding: 15;");
        usersBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-alignment: center-left; -fx-padding: 15;"); // Thêm dòng này

        // Set active button style
        activeButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-alignment: center-left; -fx-padding: 15;");
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
