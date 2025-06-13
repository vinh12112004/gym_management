package com.gymapp.controller.Member;

import com.gymapp.api.ApiClient;
import com.gymapp.api.ApiConfig;
import com.gymapp.model.Member;
import com.gymapp.util.AlertHelper;
import com.gymapp.util.ValidationUtil;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class MemberFormController implements Initializable {

    @FXML private Label titleLabel;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private DatePicker joinDatePicker;
    @FXML private ComboBox<String> membershipComboBox;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;
    @FXML private ProgressIndicator progressIndicator;

    private Member member;
    private MemberController parentController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        membershipComboBox.setItems(FXCollections.observableArrayList(
                "Basic", "Premium", "VIP", "Student", "Senior"));
        membershipComboBox.setValue("Basic");

        statusComboBox.setItems(FXCollections.observableArrayList(
                "Active", "Inactive", "Suspended", "Expired"));
        statusComboBox.setValue("Active");

        joinDatePicker.setValue(LocalDate.now());
    }

    public void setMember(Member member) {
        this.member = member;
        if (member != null) {
            titleLabel.setText("Edit Member");
            firstNameField.setText(member.getFirstName());
            lastNameField.setText(member.getLastName());
            emailField.setText(member.getEmail());
            phoneField.setText(member.getPhone());
            if (member.getJoinDate() != null) {
                joinDatePicker.setValue(member.getJoinDate());
            }
            membershipComboBox.setValue(member.getMembershipType());
            statusComboBox.setValue(member.getStatus());
        }
    }

    public void setParentController(MemberController parentController) {
        this.parentController = parentController;
    }

    @FXML
    private void handleSave() {
        if (!validateInput()) return;

        setLoading(true);
        new Thread(new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Member toSave = createMemberFromForm();
                HttpResponse<String> response;
                // --- đổi MEMBER thành MEMBERS ---
                if (member == null) {
                    response = ApiClient.getInstance()
                            .post(ApiConfig.MEMBERS, toSave);
                } else {
                    toSave.setId(member.getId());
                    response = ApiClient.getInstance()
                            .put(ApiConfig.MEMBERS + "/" + member.getId(), toSave);
                }

                if (response.statusCode() == 200 || response.statusCode() == 201) {
                    Platform.runLater(() -> {
                        AlertHelper.showInfo("Success", "Member saved successfully.");
                        if (parentController != null) {
                            parentController.onMemberSaved();
                        }
                        closeWindow();
                    });
                } else {
                    Platform.runLater(() ->
                            AlertHelper.showError("Error", "Failed to save member.")
                    );
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
                    AlertHelper.showError("Error", "Connection failed. Please try again.");
                });
            }
        }).start();
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private boolean validateInput() {
        if (!ValidationUtil.isNotEmpty(firstNameField.getText())) {
            AlertHelper.showWarning("Validation Error", "First name is required.");
            return false;
        }
        if (!ValidationUtil.isNotEmpty(lastNameField.getText())) {
            AlertHelper.showWarning("Validation Error", "Last name is required.");
            return false;
        }
        if (!ValidationUtil.isValidEmail(emailField.getText())) {
            AlertHelper.showWarning("Validation Error", "Please enter a valid email address.");
            return false;
        }
        if (!ValidationUtil.isValidPhone(phoneField.getText())) {
            AlertHelper.showWarning("Validation Error", "Please enter a valid phone number.");
            return false;
        }
        if (joinDatePicker.getValue() == null) {
            AlertHelper.showWarning("Validation Error", "Join date is required.");
            return false;
        }
        return true;
    }

    private Member createMemberFromForm() {
        Member m = new Member();
        m.setFirstName(firstNameField.getText().trim());
        m.setLastName(lastNameField.getText().trim());
        m.setEmail(emailField.getText().trim());
        m.setPhone(phoneField.getText().trim());
        m.setJoinDate(joinDatePicker.getValue());
        m.setMembershipType(membershipComboBox.getValue());
        m.setStatus(statusComboBox.getValue());
        return m;
    }

    private void setLoading(boolean loading) {
        Platform.runLater(() -> {
            progressIndicator.setVisible(loading);
            saveButton.setDisable(loading);
            cancelButton.setDisable(loading);
        });
    }

    private void closeWindow() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}
