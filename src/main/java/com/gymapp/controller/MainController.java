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
    @FXML private Button feedbackBtn;
    @FXML private Button usersBtn;
    @FXML private Button staffBtn;
    @FXML private Button profileBtn;
    @FXML private Button membershipPakagesBtn;
    @FXML private Button memberPakagesBtn;
    @FXML private Button traineesBtn;
    @FXML private Button workoutHistoryBtn;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("profileBtn is null? " + (profileBtn == null));
        userLabel.setText("Welcome, " + SessionManager.getInstance().getCurrentUser());
        String role = SessionManager.getInstance().getCurrentRole();
        if ("TRAINER".equalsIgnoreCase(role)) {
            dashboardBtn.setVisible(false);
            dashboardBtn.setManaged(false);
            membersBtn.setVisible(false);
            membersBtn.setManaged(false); // Ẩn hoàn toàn khỏi layout
            staffBtn.setVisible(false);
            staffBtn.setManaged(false);
            equipmentBtn.setVisible(false);
            equipmentBtn.setManaged(false);
            packagesBtn.setVisible(false);
            packagesBtn.setManaged(false);
            memberPakagesBtn.setVisible(false);
            memberPakagesBtn.setManaged(false);
            feedbackBtn.setVisible(false);
            roomsBtn.setVisible(false);
            roomsBtn.setManaged(false);
            membershipPakagesBtn.setVisible(false);
            membershipPakagesBtn.setManaged(false);
            feedbackBtn.setManaged(false);
            usersBtn.setVisible(false);
            usersBtn.setManaged(false);
            workoutHistoryBtn.setManaged(false);
            workoutHistoryBtn.setVisible(false);
        }
        if("MEMBER".equalsIgnoreCase(role)) {
            dashboardBtn.setVisible(false);
            dashboardBtn.setManaged(false);
            staffBtn.setVisible(false);
            staffBtn.setManaged(false);
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
            membershipPakagesBtn.setVisible(false);
            membershipPakagesBtn.setManaged(false);
            traineesBtn.setVisible(false);
            traineesBtn.setManaged(false);
        }
        if("MANAGER".equalsIgnoreCase(role)) {

            roomsBtn.setVisible(false);
            roomsBtn.setManaged(false);
            equipmentBtn.setVisible(false);
            equipmentBtn.setManaged(false);
            profileBtn.setVisible(false);
            profileBtn.setManaged(false);
            memberPakagesBtn.setVisible(false);
            memberPakagesBtn.setManaged(false);
            traineesBtn.setVisible(false);
            traineesBtn.setManaged(false);
        }
        if("OWNER".equalsIgnoreCase(role)) {
            profileBtn.setVisible(false);
            profileBtn.setManaged(false);
            memberPakagesBtn.setVisible(false);
            memberPakagesBtn.setManaged(false);
            traineesBtn.setVisible(false);
            traineesBtn.setManaged(false);
            workoutHistoryBtn.setManaged(false);
            workoutHistoryBtn.setVisible(false);
            membershipPakagesBtn.setVisible(false);
            membershipPakagesBtn.setManaged(false);
        }
        System.out.println("Role: " + role);
        System.out.println("ProfileBtn visible: " + profileBtn.isVisible());
        showDashboard(); // Show dashboard by default
    }

    @FXML
    private void handleLogout() {
        SessionManager.getInstance().logout();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Auth/Login.fxml"));
            Scene scene = new Scene(loader.load(), 1200, 800);
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
        loadContent("/fxml/Dashboard/Dashboard.fxml");
        setActiveButton(dashboardBtn);
    }

    @FXML
    private void showEquipment() {
        loadContent("/fxml/Equipment/Equipment.fxml");
        setActiveButton(equipmentBtn);
    }

    @FXML
    private void showMembers() {
        loadContent("/fxml/Member/Members.fxml");
        setActiveButton(membersBtn);
    }

    @FXML
    private void showPackages() {
        loadContent("/fxml/Package/Packages.fxml");
        setActiveButton(packagesBtn);
    }

    @FXML
    private void showRooms() {
        loadContent("/fxml/Room/Rooms.fxml");
        setActiveButton(roomsBtn);
    }

    @FXML
    private void showFeedback() {
        loadContent("/fxml/Feedback/Feedback.fxml");
        setActiveButton(feedbackBtn);
    }

    @FXML
    private void showUsers() {
        loadContent("/fxml/User/Users.fxml");
        setActiveButton(usersBtn);
    }

    @FXML
    private void showStaff() {
        loadContent("/fxml/Staff/Staff.fxml");
        setActiveButton(staffBtn);
    }
    @FXML
    private void showProfile() {
        loadContent("/fxml/Profile/ProfileForm.fxml");
        setActiveButton(profileBtn);
    }

    @FXML
    private void showMembershipPakages(){
        loadContent("/fxml/MembershipPackage/MembershipPackage.fxml");
        setActiveButton(membershipPakagesBtn);
    }

    @FXML
    private void showMemberPakages() {
        loadContent("/fxml/MemberPackage/MemberPackages.fxml");
        setActiveButton(memberPakagesBtn);
    }

    @FXML
    private void showTrainees() {
        loadContent("/fxml/Trainee/Trainees.fxml");
        setActiveButton(traineesBtn);
    }

    @FXML
    private void showWorkoutHistory() {
        loadContent("/fxml/WorkoutHistory/WorkoutHistory.fxml");
        setActiveButton(workoutHistoryBtn);
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
        Button[] buttons = {
            dashboardBtn, equipmentBtn, membersBtn, packagesBtn, roomsBtn,
             feedbackBtn, usersBtn, staffBtn, profileBtn,
            membershipPakagesBtn, memberPakagesBtn, traineesBtn
        };
        for (Button btn : buttons) {
            if (btn != null) btn.getStyleClass().remove("active");
        }
        if (activeButton != null && !activeButton.getStyleClass().contains("active")) {
            activeButton.getStyleClass().add("active");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
