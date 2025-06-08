package com.gymapp.controller;

import com.gymapp.api.ApiClient;
import com.gymapp.api.ApiConfig;
import com.gymapp.model.Package;
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
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class PackageFormController implements Initializable {

    @FXML private Label titleLabel;
    @FXML private TextField nameField;
    @FXML private TextField priceField;
    @FXML private TextField durationField;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private TextArea descriptionArea;
    @FXML private TextArea featuresArea;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;
    @FXML private ProgressIndicator progressIndicator;

    private Package packageObj;
    private PackageController parentController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Khởi tạo combobox với các trạng thái
        statusComboBox.setItems(FXCollections.observableArrayList("ACTIVE", "INACTIVE", "DISCONTINUED"));
        statusComboBox.setValue("ACTIVE");
    }

    public void setPackage(Package packageObj) {
        this.packageObj = packageObj;
        if (packageObj != null) {
            titleLabel.setText("Edit Package");
            nameField.setText(packageObj.getName());
            if (packageObj.getPrice() != null) {
                priceField.setText(packageObj.getPrice().toString());
            }
            if (packageObj.getDurationMonths() != null) {
                durationField.setText(packageObj.getDurationMonths().toString());
            }
            statusComboBox.setValue(packageObj.getStatus());
            descriptionArea.setText(packageObj.getDescription());
            if (packageObj.getFeatures() != null) {
                featuresArea.setText(String.join("\n", packageObj.getFeatures()));
            }
        }
    }

    public void setParentController(PackageController parentController) {
        this.parentController = parentController;
    }

    @FXML
    private void handleSave() {
        if (!validateInput()) return;

        setLoading(true);
        Task<Void> saveTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                Package pkg = createPackageFromForm();
                HttpResponse<String> response;

                // Chuyển sang dùng hằng số số nhiều
                if (packageObj == null) {
                    response = ApiClient.getInstance().post(ApiConfig.PACKAGES, pkg);
                } else {
                    pkg.setId(packageObj.getId());
                    response = ApiClient.getInstance().put(
                            ApiConfig.PACKAGES + "/" + packageObj.getId(), pkg
                    );
                }

                if (response.statusCode() == 200 || response.statusCode() == 201) {
                    Platform.runLater(() -> {
                        AlertHelper.showInfo("Success", "Package saved successfully.");
                        if (parentController != null) {
                            parentController.onPackageSaved();
                        }
                        closeWindow();
                    });
                } else {
                    Platform.runLater(() ->
                            AlertHelper.showError("Error", "Failed to save package.")
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
        };
        new Thread(saveTask).start();
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private boolean validateInput() {
        if (!ValidationUtil.isNotEmpty(nameField.getText())) {
            AlertHelper.showWarning("Validation Error", "Package name is required.");
            return false;
        }
        if (!ValidationUtil.isValidNumber(priceField.getText())) {
            AlertHelper.showWarning("Validation Error", "Please enter a valid price.");
            return false;
        }
        if (!ValidationUtil.isValidInteger(durationField.getText())) {
            AlertHelper.showWarning("Validation Error", "Please enter a valid duration in months.");
            return false;
        }
        if (!ValidationUtil.isNotEmpty(descriptionArea.getText())) {
            AlertHelper.showWarning("Validation Error", "Description is required.");
            return false;
        }
        return true;
    }

    private Package createPackageFromForm() {
        Package pkg = new Package();
        pkg.setName(nameField.getText().trim());
        pkg.setPrice(Double.parseDouble(priceField.getText().trim()));
        pkg.setDurationMonths(Integer.parseInt(durationField.getText().trim()));
        pkg.setStatus(statusComboBox.getValue());
        pkg.setDescription(descriptionArea.getText().trim());

        String featuresText = featuresArea.getText().trim();
        if (!featuresText.isEmpty()) {
            List<String> features = Arrays.stream(featuresText.split("\\R"))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
            pkg.setFeatures(features);
        }

        return pkg;
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
