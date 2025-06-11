package com.gymapp.controller.Equipment;

import com.fasterxml.jackson.core.type.TypeReference;
import com.gymapp.api.ApiClient;
import com.gymapp.api.ApiConfig;
import com.gymapp.model.Equipment;
import com.gymapp.model.Room;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.ResourceBundle;

public class EquipmentFormController implements Initializable {

    @FXML private Label titleLabel;
    @FXML private ComboBox<Room> roomComboBox;
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
        // Load status options
        statusComboBox.setItems(FXCollections.observableArrayList(
                "ACTIVE", "MAINTENANCE", "OUT_OF_ORDER", "RETIRED"
        ));
        statusComboBox.setValue("ACTIVE");

        // Load rooms for assignment
        progressIndicator.setVisible(true);
        Task<Void> loadRoomsTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                HttpResponse<String> resp = ApiClient.getInstance().get(ApiConfig.ROOMS);
                if (resp.statusCode() == 200) {
                    List<Room> rooms = ApiClient.getInstance()
                            .getObjectMapper()
                            .readValue(resp.body(), new TypeReference<List<Room>>() {});
                    Platform.runLater(() -> {
                        ObservableList<Room> items = FXCollections.observableArrayList(rooms);
                        roomComboBox.setItems(items);
                        // Nếu đang ở chế độ sửa, chọn đúng phòng
                        if (equipment != null && equipment.getRoom() != null) {
                            roomComboBox.setValue(equipment.getRoom());
                        } else if (!items.isEmpty()) {
                            roomComboBox.setValue(items.get(0));
                        }
                    });
                } else {
                    Platform.runLater(() ->
                            showAlert("Error", "Failed to load rooms.", Alert.AlertType.ERROR)
                    );
                }
                return null;
            }
            @Override protected void succeeded() { progressIndicator.setVisible(false); }
            @Override protected void failed() {
                Platform.runLater(() -> {
                    progressIndicator.setVisible(false);
                    showAlert("Error", "Connection failed. Please try again.", Alert.AlertType.ERROR);
                });
            }
        };
        new Thread(loadRoomsTask).start();
    }

    /** Called by parent to switch into Edit mode */
    public void setEquipment(Equipment equipment) {
        this.equipment = equipment;
        if (equipment != null) {
            titleLabel.setText("Edit Equipment");
            // select room (sẽ được set lại khi loadRoomsTask chạy xong)
            if (roomComboBox.getItems() != null && !roomComboBox.getItems().isEmpty() && equipment.getRoom() != null) {
                roomComboBox.setValue(equipment.getRoom());
            }
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
                        if (parentController != null) parentController.onEquipmentSaved();
                        closeWindow();
                    });
                } else {
                    Platform.runLater(() ->
                            showAlert("Error", "Failed to save equipment.", Alert.AlertType.ERROR)
                    );
                }
                return null;
            }

            @Override protected void succeeded() { setLoading(false); }
            @Override protected void failed() {
                Platform.runLater(() -> {
                    setLoading(false);
                    showAlert("Error", "Connection failed. Please try again.", Alert.AlertType.ERROR);
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
        if (roomComboBox.getValue() == null) {
            showAlert("Validation Error", "Please select a room.", Alert.AlertType.WARNING);
            return false;
        }
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
        e.setRoom(roomComboBox.getValue());
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

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert a = new Alert(type);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(message);
        a.showAndWait();
    }

    private void closeWindow() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}