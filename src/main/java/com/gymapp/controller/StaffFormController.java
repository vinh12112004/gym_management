package com.gymapp.controller;

import com.gymapp.api.ApiClient;
import com.gymapp.api.ApiConfig;
import com.gymapp.model.Staff;
import com.gymapp.util.AlertHelper;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.http.HttpResponse;
import java.net.URL;
import java.util.ResourceBundle;

public class StaffFormController implements Initializable {

    @FXML private Label titleLabel;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private ComboBox<String> positionComboBox;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;
    @FXML private ProgressIndicator progressIndicator;

    private Staff staff;
    private StaffController parentController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        positionComboBox.setItems(FXCollections.observableArrayList("MANAGER", "TRAINER"));
        positionComboBox.setValue("TRAINER");
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
        if (staff != null) {
            titleLabel.setText("Edit Staff");
            firstNameField.setText(staff.getFirstName());
            lastNameField.setText(staff.getLastName());
            emailField.setText(staff.getEmail());
            phoneField.setText(staff.getPhone());
            positionComboBox.setValue(staff.getPosition());
        }
    }

    public void setParentController(StaffController parentController) {
        this.parentController = parentController;
    }

    @FXML
    private void handleSave() {
        if (!validateInput()) return;

        setLoading(true);
        new Thread(new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Staff toSave = createStaffFromForm();
                HttpResponse<String> response;
                if (staff == null) {
                    response = ApiClient.getInstance()
                            .post(ApiConfig.STAFFS, toSave);
                } else {
                    toSave.setId(staff.getId());
                    response = ApiClient.getInstance()
                            .put(ApiConfig.STAFFS + "/" + staff.getId(), toSave);
                }

                if (response.statusCode() == 200 || response.statusCode() == 201) {
                    Platform.runLater(() -> {
                        AlertHelper.showInfo("Success", "Staff saved successfully.");
                        if (parentController != null) {
                            parentController.refreshData();
                        }
                        closeWindow();
                    });
                } else {
                    Platform.runLater(() ->
                            AlertHelper.showError("Error", "Failed to save staff.")
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
        if (firstNameField.getText().trim().isEmpty()) {
            AlertHelper.showWarning("Validation Error", "First name is required.");
            return false;
        }
        if (lastNameField.getText().trim().isEmpty()) {
            AlertHelper.showWarning("Validation Error", "Last name is required.");
            return false;
        }
        if (emailField.getText().trim().isEmpty()) {
            AlertHelper.showWarning("Validation Error", "Email is required.");
            return false;
        }
        if (positionComboBox.getValue() == null) {
            AlertHelper.showWarning("Validation Error", "Position is required.");
            return false;
        }
        return true;
    }

    private Staff createStaffFromForm() {
        Staff s = new Staff();
        s.setFirstName(firstNameField.getText().trim());
        s.setLastName(lastNameField.getText().trim());
        s.setEmail(emailField.getText().trim());
        s.setPhone(phoneField.getText().trim());
        s.setPosition(positionComboBox.getValue());
        return s;
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