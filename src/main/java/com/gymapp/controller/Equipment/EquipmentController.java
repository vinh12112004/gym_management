package com.gymapp.controller.Equipment;

import com.fasterxml.jackson.core.type.TypeReference;
import com.gymapp.api.ApiClient;
import com.gymapp.api.ApiConfig;
import com.gymapp.model.Equipment;
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

public class EquipmentController implements Initializable {

    @FXML private TableView<Equipment> equipmentTable;
    @FXML private TableColumn<Equipment, Long> idColumn;
    @FXML private TableColumn<Equipment, String> nameColumn;
    @FXML private TableColumn<Equipment, String> typeColumn;
    @FXML private TableColumn<Equipment, String> statusColumn;
    @FXML private TableColumn<Equipment, Double> priceColumn;
    @FXML private TableColumn<Equipment, Void> actionsColumn;
    @FXML private Button addButton;
    @FXML private Button editButton;
    @FXML private Button deleteButton;
    @FXML private Button refreshButton;
    @FXML private ProgressIndicator progressIndicator;

    private ObservableList<Equipment> equipmentList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTable();
        refreshData();
    }

    private void setupTable() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        equipmentTable.setItems(equipmentList);
        equipmentTable.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            boolean sel = n != null;
            editButton.setDisable(!sel);
            deleteButton.setDisable(!sel);
        });
    }

    @FXML
    private void refreshData() {
        setLoading(true);
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                // Đổi từ ApiConfig.EQUIPMENT -> ApiConfig.EQUIPMENTS
                HttpResponse<String> response = ApiClient.getInstance().get(ApiConfig.EQUIPMENTS);

                if (response.statusCode() == 200) {
                    List<Equipment> list = ApiClient.getInstance().getObjectMapper()
                            .readValue(response.body(), new TypeReference<List<Equipment>>() {});
                    Platform.runLater(() -> {
                        equipmentList.setAll(list);
                    });
                } else {
                    Platform.runLater(() -> showAlert("Error", "Failed to load equipment data."));
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
        new Thread(task).start();
    }

    @FXML private void handleAdd()  { openEquipmentForm(null); }
    @FXML private void handleEdit() {
        Equipment sel = equipmentTable.getSelectionModel().getSelectedItem();
        if (sel != null) openEquipmentForm(sel);
    }
    @FXML private void handleDelete() {
        Equipment sel = equipmentTable.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        Alert c = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure you want to delete this equipment?", ButtonType.OK, ButtonType.CANCEL);
        c.setHeaderText(null);
        if (c.showAndWait().filter(bt -> bt==ButtonType.OK).isPresent()) {
            deleteEquipment(sel);
        }
    }

    private void deleteEquipment(Equipment equipment) {
        setLoading(true);
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                // Đổi từ ApiConfig.EQUIPMENT -> ApiConfig.EQUIPMENTS
                HttpResponse<String> resp = ApiClient.getInstance()
                        .delete(ApiConfig.EQUIPMENTS + "/" + equipment.getId());

                if (resp.statusCode() == 200 || resp.statusCode() == 204) {
                    Platform.runLater(() -> {
                        equipmentList.remove(equipment);
                        showAlert("Success", "Deleted successfully.", Alert.AlertType.INFORMATION);
                    });
                } else {
                    Platform.runLater(() -> showAlert("Error", "Failed to delete equipment."));
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
        new Thread(task).start();
    }

    private void openEquipmentForm(Equipment equipment) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Equipment/EquipmentForm.fxml"));
            Scene scene = new Scene(loader.load());
            EquipmentFormController ctr = loader.getController();
            ctr.setEquipment(equipment);
            ctr.setParentController(this);

            Stage stage = new Stage();
            stage.setTitle(equipment == null ? "Add Equipment" : "Edit Equipment");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.showAndWait();
        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert("Error", "Could not load equipment form.");
        }
    }

    public void onEquipmentSaved() { refreshData(); }

    private void setLoading(boolean loading) {
        Platform.runLater(() -> {
            progressIndicator.setVisible(loading);
            addButton.setDisable(loading);
            editButton.setDisable(loading);
            deleteButton.setDisable(loading);
            refreshButton.setDisable(loading);
        });
    }

    private void showAlert(String title, String msg) {
        showAlert(title, msg, Alert.AlertType.ERROR);
    }
    private void showAlert(String title, String msg, Alert.AlertType type) {
        Alert a = new Alert(type);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}
