package com.gymapp.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.gymapp.api.ApiClient;
import com.gymapp.api.ApiConfig;
import com.gymapp.model.Staff;
import com.gymapp.util.AlertHelper;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.ResourceBundle;

public class StaffController implements Initializable {

    @FXML private TableView<Staff> staffTable;
    @FXML private TableColumn<Staff, Long> idColumn;
    @FXML private TableColumn<Staff, String> firstNameColumn;
    @FXML private TableColumn<Staff, String> lastNameColumn;
    @FXML private TableColumn<Staff, String> emailColumn;
    @FXML private TableColumn<Staff, String> phoneColumn;
    @FXML private TableColumn<Staff, String> positionColumn;
    @FXML private Button addButton;
    @FXML private Button editButton;
    @FXML private Button deleteButton;
    @FXML private Button refreshButton;
    @FXML private ProgressIndicator progressIndicator;

    private final ObservableList<Staff> staffList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTable();
        refreshData();
    }

    private void setupTable() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        positionColumn.setCellValueFactory(new PropertyValueFactory<>("position"));

        staffTable.setItems(staffList);

        staffTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            boolean sel = newSel != null;
            editButton.setDisable(!sel);
            deleteButton.setDisable(!sel);
        });
    }

    @FXML
    public void refreshData() {
        setLoading(true);
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                HttpResponse<String> response = ApiClient.getInstance()
                        .get(ApiConfig.STAFFS);

                if (response.statusCode() == 200) {
                    List<Staff> list = ApiClient.getInstance()
                            .getObjectMapper()
                            .readValue(response.body(), new TypeReference<List<Staff>>() {});

                    Platform.runLater(() -> {
                        staffList.setAll(list);
                    });
                } else {
                    Platform.runLater(() ->
                            AlertHelper.showError("Error", "Failed to load staff data.")
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
        new Thread(task).start();
    }

    @FXML private void handleAdd()    { openStaffForm(null); }
    @FXML private void handleEdit()   {
        Staff sel = staffTable.getSelectionModel().getSelectedItem();
        if (sel != null) openStaffForm(sel);
    }
    @FXML private void handleDelete() {
        Staff sel = staffTable.getSelectionModel().getSelectedItem();
        if (sel != null && AlertHelper.showConfirmation(
                "Confirm Delete",
                "Are you sure you want to delete this staff?")) {
            deleteStaff(sel);
        }
    }

    private void deleteStaff(Staff staff) {
        setLoading(true);
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                HttpResponse<String> resp = ApiClient.getInstance()
                        .delete(ApiConfig.STAFFS + "/" + staff.getId());

                if (resp.statusCode() == 200 || resp.statusCode() == 204) {
                    Platform.runLater(() -> {
                        staffList.remove(staff);
                        AlertHelper.showInfo("Success", "Staff deleted successfully.");
                    });
                } else {
                    Platform.runLater(() ->
                            AlertHelper.showError("Error", "Failed to delete staff.")
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
        new Thread(task).start();
    }

    private void openStaffForm(Staff staff) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/StaffForm.fxml"));
            Scene scene = new Scene(loader.load(), 400, 400);
            StaffFormController ctl = loader.getController();
            ctl.setStaff(staff);
            ctl.setParentController(this);

            Stage stage = new Stage();
            stage.setTitle(staff == null ? "Add Staff" : "Edit Staff");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.showAndWait();
        } catch (Exception e) {
            AlertHelper.showError("Error", "Could not load staff form.");
        }
    }

    public void refreshDataFromChild() {
        refreshData();
    }

    private void setLoading(boolean loading) {
        Platform.runLater(() -> {
            progressIndicator.setVisible(loading);
            addButton.setDisable(loading);
            editButton.setDisable(loading);
            deleteButton.setDisable(loading);
            refreshButton.setDisable(loading);
        });
    }
}