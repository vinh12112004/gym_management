package com.gymapp.controller.User;

import com.gymapp.api.ApiClient;
import com.gymapp.api.ApiConfig;
import com.gymapp.model.User;
import com.gymapp.util.AlertHelper;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class UserFormController {
    @FXML private Label titleLabel;
    @FXML private TextField emailField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<String> roleComboBox;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;
    @FXML private ProgressIndicator progressIndicator;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField phoneField;

    private User user;

    @FXML
    public void initialize() {
        roleComboBox.setItems(FXCollections.observableArrayList("OWNER", "MANAGER", "TRAINER", "MEMBER"));
        roleComboBox.getSelectionModel().selectFirst();
    }

    public void setUser(User user) {
        this.user = user;
        if (user != null) {
            titleLabel.setText("Edit User");
            emailField.setText(user.getEmail());
            usernameField.setText(user.getUsername());
            usernameField.setDisable(true);
            emailField.setDisable(true);
            roleComboBox.setValue(user.getFirstRole());
            passwordField.setPromptText("Leave blank to keep current password");
            firstNameField.setVisible(false);
            lastNameField.setVisible(false);
            phoneField.setVisible(false);
            ((Label) firstNameField.getParent().lookup("#firstNameLabel")).setVisible(false);
            ((Label) lastNameField.getParent().lookup("#lastNameLabel")).setVisible(false);
            ((Label) phoneField.getParent().lookup("#phoneLabel")).setVisible(false);
        } else {
            firstNameField.setVisible(true);
            lastNameField.setVisible(true);
            phoneField.setVisible(true);
            ((Label) firstNameField.getParent().lookup("#firstNameLabel")).setVisible(true);
            ((Label) lastNameField.getParent().lookup("#lastNameLabel")).setVisible(true);
            ((Label) phoneField.getParent().lookup("#phoneLabel")).setVisible(true);
        }
    }

    @FXML
    private void handleSave() {
        String email = emailField.getText().trim();
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String role = roleComboBox.getValue();
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String phone = phoneField.getText().trim();

        if (email.isEmpty() || username.isEmpty() || role == null) {
            AlertHelper.showWarning("Validation Error", "Please fill all required fields.");
            return;
        }

        setLoading(true);
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                boolean isEdit = user != null;
                var reqMap = new java.util.HashMap<String, Object>();
                reqMap.put("email", email);
                reqMap.put("username", username);

                // Gửi roles là mảng object
                var roleObj = new java.util.HashMap<String, String>();
                roleObj.put("name", role);
                reqMap.put("roles", java.util.List.of(roleObj));

                if (!isEdit) {
                    reqMap.put("password", password);
                    reqMap.put("firstName", firstName);
                    reqMap.put("lastName", lastName);
                    reqMap.put("phone", phone);
                } else {
                    // Khi edit, chỉ gửi password nếu có nhập
                    if (!password.isEmpty()) {
                        reqMap.put("password", password);
                    }
                }

                var resp = isEdit
                        ? ApiClient.getInstance().put(ApiConfig.USERS + "/" + user.getId(), reqMap)
                        : ApiClient.getInstance().post(ApiConfig.AUTH_REGISTER, reqMap);

                Platform.runLater(() -> {
                    setLoading(false);
                    if (resp.statusCode() == 200 || resp.statusCode() == 201) {
                        AlertHelper.showInfo("Success", "User saved successfully.");
                        closeWindow();
                    } else {
                        AlertHelper.showError("Error", "Failed to save user.");
                    }
                });
                return null;
            }
        };
        new Thread(task).start();
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void setLoading(boolean loading) {
        progressIndicator.setVisible(loading);
        saveButton.setDisable(loading);
        cancelButton.setDisable(loading);
    }

    private void closeWindow() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}