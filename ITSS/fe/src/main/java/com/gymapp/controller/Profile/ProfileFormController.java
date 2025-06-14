package com.gymapp.controller.Profile;

import com.gymapp.api.ApiClient;
import com.gymapp.api.ApiConfig;
import com.gymapp.model.User;
import com.gymapp.model.Member;
import com.gymapp.model.Staff;
import com.gymapp.util.AlertHelper;
import com.gymapp.util.SessionManager;
import com.fasterxml.jackson.databind.JsonNode;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class ProfileFormController {
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private PasswordField passwordField;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;
    @FXML private ProgressIndicator progressIndicator;

    private User currentUser;
    private Member currentMember;
    private Staff currentStaff;
    private String currentRole;

    @FXML
    public void initialize() {
        String email = SessionManager.getInstance().getEmail();
        currentRole = SessionManager.getInstance().getCurrentRole();

        // Lấy thông tin user
        new Thread(() -> {
            try {
                var resp = ApiClient.getInstance().get(ApiConfig.USERS + "/email/" + email);
                System.out.println("User API response: " + resp.body());
                if (resp.statusCode() == 200) {
                    try {
                        // Thử parse sang User, nếu lỗi thì chỉ lấy email
                        User user = ApiClient.getInstance().getObjectMapper().readValue(resp.body(), User.class);
                        currentUser = user;
                        Platform.runLater(() -> emailField.setText(user.getEmail()));
                    } catch (Exception ex) {
                        // Nếu không parse được User, thử lấy email từ JsonNode
                        JsonNode node = ApiClient.getInstance().getObjectMapper().readTree(resp.body());
                        String emailValue = node.has("email") ? node.get("email").asText() : "";
                        Platform.runLater(() -> emailField.setText(emailValue));
                    }
                }
            } catch (Exception e) {
                Platform.runLater(() -> AlertHelper.showError("Error", "Failed to parse user info."));
            }
        }).start();

        // Lấy thông tin member hoặc staff nếu có
        if ("MEMBER".equalsIgnoreCase(currentRole)) {
            new Thread(() -> {
                try {
                    var resp = ApiClient.getInstance().get(ApiConfig.MEMBERS + "/email/" + email);
                    System.out.println("Member API response: " + resp.body());
                    if (resp.statusCode() == 200) {
                        Member member = ApiClient.getInstance().getObjectMapper().readValue(resp.body(), Member.class);
                        currentMember = member;
                        Platform.runLater(() -> {
                            firstNameField.setText(member.getFirstName());
                            lastNameField.setText(member.getLastName());
                            phoneField.setText(member.getPhone());
                        });
                    }
                } catch (Exception e) {
                    Platform.runLater(() -> AlertHelper.showError("Error", "Failed to parse member info."));
                }
            }).start();
        } else if ("STAFF".equalsIgnoreCase(currentRole) || "MANAGER".equalsIgnoreCase(currentRole) || "TRAINER".equalsIgnoreCase(currentRole)) {
            new Thread(() -> {
                try {
                    var resp = ApiClient.getInstance().get(ApiConfig.STAFFS + "/email/" + email);
                    if (resp.statusCode() == 200) {
                        Staff staff = ApiClient.getInstance().getObjectMapper().readValue(resp.body(), Staff.class);
                        currentStaff = staff;
                        Platform.runLater(() -> {
                            firstNameField.setText(staff.getFirstName());
                            lastNameField.setText(staff.getLastName());
                            phoneField.setText(staff.getPhone());
                        });
                    }
                } catch (Exception e) {
                    Platform.runLater(() -> AlertHelper.showError("Error", "Failed to parse staff info."));
                }
            }).start();
        }
    }

    @FXML
    private void handleSave() {
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String phone = phoneField.getText().trim();

        if (email.isEmpty()) {
            AlertHelper.showWarning("Validation Error", "Email is required.");
            return;
        }

        setLoading(true);

        new Thread(() -> {
            try {
                // Cập nhật user (email, password)
                var reqMap = new java.util.HashMap<String, Object>();
                reqMap.put("email", email);
                if (!password.isEmpty()) {
                    reqMap.put("password", password);
                }
                var resp = ApiClient.getInstance().put(ApiConfig.USERS + "/email/" + email, reqMap);

                // Cập nhật member hoặc staff (firstName, lastName, phone)
                if ("MEMBER".equalsIgnoreCase(currentRole) && currentMember != null) {
                    var memberMap = new java.util.HashMap<String, Object>();
                    memberMap.put("firstName", firstName);
                    memberMap.put("lastName", lastName);
                    memberMap.put("phone", phone);
                    ApiClient.getInstance().put(ApiConfig.MEMBERS + "/email/" + email, memberMap); // Sửa ở đây
                } else if (( "STAFF".equalsIgnoreCase(currentRole) || "MANAGER".equalsIgnoreCase(currentRole) || "TRAINER".equalsIgnoreCase(currentRole) ) && currentStaff != null) {
                    var staffMap = new java.util.HashMap<String, Object>();
                    staffMap.put("firstName", firstName);
                    staffMap.put("lastName", lastName);
                    staffMap.put("phone", phone);
                    ApiClient.getInstance().put(ApiConfig.STAFFS + "/email/" + email, staffMap); // Sửa ở đây
                }

                Platform.runLater(() -> {
                    setLoading(false);
                    if (resp.statusCode() == 200) {
                        AlertHelper.showInfo("Success", "Profile updated successfully.");
                    } else {
                        AlertHelper.showError("Error", "Failed to update profile.");
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    setLoading(false);
                    AlertHelper.showError("Error", "An error occurred: " + e.getMessage());
                });
            }
        }).start();
    }

    @FXML
    private void handleCancel() {
        // Đóng form hoặc clear field nếu cần
    }

    private void setLoading(boolean loading) {
        progressIndicator.setVisible(loading);
        saveButton.setDisable(loading);
        cancelButton.setDisable(loading);
    }
}