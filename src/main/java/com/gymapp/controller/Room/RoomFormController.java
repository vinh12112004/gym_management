package com.gymapp.controller.Room;

import com.gymapp.api.ApiClient;
import com.gymapp.api.ApiConfig;
import com.gymapp.model.Room;
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
import java.util.ResourceBundle;

public class RoomFormController implements Initializable {

    @FXML private Label titleLabel;
    @FXML private TextField nameField;
    @FXML private ComboBox<String> typeComboBox;
    @FXML private TextField capacityField;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private TextField hourlyRateField;
    @FXML private TextArea descriptionArea;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;
    @FXML private ProgressIndicator progressIndicator;

    private Room room;
    private RoomController parentController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        typeComboBox.setItems(FXCollections.observableArrayList(
                "Cardio", "Weight Training", "Group Fitness", "Yoga", "Spinning", "Boxing", "Swimming Pool", "Sauna"
        ));
        typeComboBox.setValue("Cardio");

        statusComboBox.setItems(FXCollections.observableArrayList(
                "Available", "Occupied", "Maintenance", "Closed"
        ));
        statusComboBox.setValue("Available");
    }

    public void setRoom(Room room) {
        this.room = room;
        if (room != null) {
            titleLabel.setText("Edit Room");
            nameField.setText(room.getName());
            typeComboBox.setValue(room.getType());
            if (room.getCapacity() != null) {
                capacityField.setText(room.getCapacity().toString());
            }
            statusComboBox.setValue(room.getStatus());
            if (room.getHourlyRate() != null) {
                hourlyRateField.setText(room.getHourlyRate().toString());
            }
            descriptionArea.setText(room.getDescription());
        }
    }

    public void setParentController(RoomController parentController) {
        this.parentController = parentController;
    }

    @FXML
    private void handleSave() {
        if (!validateInput()) return;

        setLoading(true);

        Task<Void> saveTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                Room toSave = createRoomFromForm();
                HttpResponse<String> response;

                // ✅ Dùng ApiConfig.ROOMS thay cho ApiConfig.ROOM
                if (room == null) {
                    response = ApiClient.getInstance()
                            .post(ApiConfig.ROOMS, toSave);
                } else {
                    toSave.setId(room.getId());
                    response = ApiClient.getInstance()
                            .put(ApiConfig.ROOMS + "/" + room.getId(), toSave);
                }

                if (response.statusCode() == 200 || response.statusCode() == 201) {
                    Platform.runLater(() -> {
                        AlertHelper.showInfo("Success", "Room saved successfully.");
                        if (parentController != null) {
                            parentController.onRoomSaved();
                        }
                        closeWindow();
                    });
                } else {
                    Platform.runLater(() ->
                            AlertHelper.showError("Error", "Failed to save room.")
                    );
                }
                return null;
            }

            @Override protected void succeeded() { setLoading(false); }
            @Override protected void failed() {
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
            AlertHelper.showWarning("Validation Error", "Room name is required.");
            return false;
        }
        if (!ValidationUtil.isValidInteger(capacityField.getText())) {
            AlertHelper.showWarning("Validation Error", "Please enter a valid capacity.");
            return false;
        }
        if (!hourlyRateField.getText().trim().isEmpty()
                && !ValidationUtil.isValidNumber(hourlyRateField.getText())) {
            AlertHelper.showWarning("Validation Error", "Please enter a valid hourly rate.");
            return false;
        }
        return true;
    }

    private Room createRoomFromForm() {
        Room r = new Room();
        r.setName(nameField.getText().trim());
        r.setType(typeComboBox.getValue());
        r.setCapacity(Integer.parseInt(capacityField.getText().trim()));
        r.setStatus(statusComboBox.getValue());
        r.setDescription(descriptionArea.getText().trim());
        if (!hourlyRateField.getText().trim().isEmpty()) {
            r.setHourlyRate(Double.parseDouble(hourlyRateField.getText().trim()));
        }
        return r;
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
