package com.gymapp.controller.Equipment;

import com.gymapp.api.ApiClient;
import com.gymapp.api.ApiConfig;
import com.gymapp.model.Equipment;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.net.http.HttpResponse;
import java.util.ResourceBundle;

public class EquipmentFormController implements Initializable {

    @FXML private Label titleLabel;
    @FXML private TextField nameField;
    @FXML private TextField typeField;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private TextField priceField;
    @FXML private DatePicker purchaseDatePicker;
    @FXML private TextArea descriptionArea;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;
    @FXML private ProgressIndicator progressIndicator;

    private Equipment equipment;
    private EquipmentController parentController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        statusComboBox.setItems(FXCollections.observableArrayList(
                "ACTIVE", "MAINTENANCE", "OUT_OF_ORDER", "RETIRED"
        ));
        statusComboBox.setValue("ACTIVE");
    }

    public void setEquipment(Equipment equipment) {
        this.equipment = equipment;
        if (equipment != null) {
            titleLabel.setText("Edit Equipment");
            nameField.setText(equipment.getName());
            typeField.setText(equipment.getType());
            statusComboBox.setValue(equipment.getStatus());
            if (equipment.getPrice() != null) {
                priceField.setText(equipment.getPrice().toString());
            }
            if (equipment.getPurchaseDate() != null) {
                purchaseDatePicker.setValue(equipment.getPurchaseDate().toLocalDate());
            }
            descriptionArea.setText(equipment.getDescription());
        }
    }

    public void setParentController(EquipmentController parentController) {
        this.parentController = parentController;
    }

    @FXML
    private void handleSave() {
        if (!validateInput()) return;

        setLoading(true);
        Task<Void> saveTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                Equipment toSave = createEquipmentFromForm();
                HttpResponse<String> response;

                // --- dùng ApiConfig.EQUIPMENTS thay cho ApiConfig.EQUIPMENT ---
                if (equipment == null) {
                    response = ApiClient.getInstance()
                            .post(ApiConfig.EQUIPMENTS, toSave);
                } else {
                    toSave.setId(equipment.getId());
                    response = ApiClient.getInstance()
                            .put(ApiConfig.EQUIPMENTS + "/" + equipment.getId(), toSave);
                }

                if (response.statusCode() == 200 || response.statusCode() == 201) {
                    Platform.runLater(() -> {
                        showAlert("Success", "Equipment saved successfully.", Alert.AlertType.INFORMATION);
                        if (parentController != null) {
                            parentController.onEquipmentSaved();
                        }
                        closeWindow();
                    });
                } else {
                    Platform.runLater(() ->
                            showAlert("Error", "Failed to save equipment.")
                    );
                }
                return null;
            }

            @Override protected void succeeded() { setLoading(false); }
            @Override protected void failed() {
                Platform.runLater(() -> {
                    setLoading(false);
                    showAlert("Error", "Connection failed. Please try again.");
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
        if (nameField.getText().trim().isEmpty()) {
            showAlert("Validation Error", "Equipment name is required.", Alert.AlertType.WARNING);
            return false;
        }
        if (typeField.getText().trim().isEmpty()) {
            showAlert("Validation Error", "Equipment type is required.", Alert.AlertType.WARNING);
            return false;
        }
        if (!priceField.getText().trim().isEmpty()) {
            try {
                Double.parseDouble(priceField.getText().trim());
            } catch (NumberFormatException e) {
                showAlert("Validation Error", "Please enter a valid price.", Alert.AlertType.WARNING);
                return false;
            }
        }
        return true;
    }

    private Equipment createEquipmentFromForm() {
        Equipment e = new Equipment();
        e.setName(nameField.getText().trim());
        e.setType(typeField.getText().trim());
        e.setStatus(statusComboBox.getValue());
        e.setDescription(descriptionArea.getText().trim());

        if (!priceField.getText().trim().isEmpty()) {
            e.setPrice(Double.parseDouble(priceField.getText().trim()));
        }
        if (purchaseDatePicker.getValue() != null) {
            e.setPurchaseDate(purchaseDatePicker.getValue().atStartOfDay());
        }
        return e;
    }

    private void setLoading(boolean loading) {
        Platform.runLater(() -> {
            progressIndicator.setVisible(loading);
            saveButton.setDisable(loading);
            cancelButton.setDisable(loading);
        });
    }

    // Giữ nguyên phương thức 3 tham số
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert a = new Alert(type);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(message);
        a.showAndWait();
    }

// Thêm overload 2 tham số để mặc định dùng ALERT ERROR
    /**
     * Hiển thị Alert loại ERROR, chỉ cần truyền title và message
     */
    private void showAlert(String title, String message) {
        showAlert(title, message, Alert.AlertType.ERROR);
    }

    private void closeWindow() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}
