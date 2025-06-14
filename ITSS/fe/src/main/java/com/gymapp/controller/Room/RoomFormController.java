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
import java.time.LocalTime;
import java.util.Map;
import java.util.ResourceBundle;

public class  RoomFormController implements Initializable {

    @FXML private Label titleLabel;
    @FXML private TextField nameField;
    @FXML private ComboBox<String> typeComboBox;
    @FXML private TextArea addressArea;
    @FXML private TextField openTimeField;
    @FXML private TextField closeTimeField;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;
    @FXML private ProgressIndicator progressIndicator;

    private Room room;
    private RoomController parentController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        typeComboBox.setItems(FXCollections.observableArrayList("Gym", "Yoga", "Fitness"));
        typeComboBox.setValue("Gym");

        statusComboBox.setItems(FXCollections.observableArrayList(
                "Available", "Occupied", "Maintenance", "Closed"
        ));
        statusComboBox.setValue("Available");

        progressIndicator.setVisible(false);
    }

    /** Gọi từ RoomController để chuyển form vào chế độ Edit hoặc Add */
    public void setRoom(Room room) {
        this.room = room;
        if (room != null) {
            titleLabel.setText("Edit Room");
            nameField.setText(room.getName());
            typeComboBox.setValue(room.getRoomType());
            addressArea.setText(room.getAddress());
            openTimeField.setText(room.getOpenTime() != null ? room.getOpenTime() : "");
            closeTimeField.setText(room.getCloseTime() != null ? room.getCloseTime() : "");
            statusComboBox.setValue(room.getRoomStatus());
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
                Map<String, Object> payload = Map.of(
                        "name",       nameField.getText().trim(),
                        "roomType",   typeComboBox.getValue(),
                        "address",    addressArea.getText().trim(),
                        "openTime",   openTimeField.getText().trim(),
                        "closeTime",  closeTimeField.getText().trim(),
                        "roomStatus", statusComboBox.getValue()
                );

                HttpResponse<String> response;
                if (room == null) {
                    response = ApiClient.getInstance().post(ApiConfig.ROOMS, payload);
                } else {
                    response = ApiClient.getInstance().put(
                            ApiConfig.ROOMS + "/" + room.getId(), payload
                    );
                }

                if (response.statusCode() == 200 || response.statusCode() == 201) {
                    Platform.runLater(() -> {
                        AlertHelper.showInfo("Success", "Room saved successfully.");
                        if (parentController != null) parentController.onRoomSaved();
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
        if (!ValidationUtil.isNotEmpty(addressArea.getText())) {
            AlertHelper.showWarning("Validation Error", "Address is required.");
            return false;
        }
        try {
            LocalTime.parse(openTimeField.getText().trim());
        } catch (Exception e) {
            AlertHelper.showWarning("Validation Error", "Open time must be HH:mm.");
            return false;
        }
        try {
            LocalTime.parse(closeTimeField.getText().trim());
        } catch (Exception e) {
            AlertHelper.showWarning("Validation Error", "Close time must be HH:mm.");
            return false;
        }
        return true;
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