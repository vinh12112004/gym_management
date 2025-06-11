package com.gymapp.controller.Equipment;

import com.fasterxml.jackson.core.type.TypeReference;
import com.gymapp.api.ApiClient;
import com.gymapp.api.ApiConfig;
import com.gymapp.model.Equipment;
import com.gymapp.model.Room;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.net.URLEncoder;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ResourceBundle;

public class EquipmentController implements Initializable {

    // --- Search/filter controls ---
    @FXML private TextField    searchField;
    @FXML private Button       searchButton;
    @FXML private Button       clearButton;
    @FXML private ComboBox<Room> roomFilterCombo;

    // --- Table + columns ---
    @FXML private TableView<Equipment> equipmentTable;
    @FXML private TableColumn<Equipment, Long>          idColumn;
    @FXML private TableColumn<Equipment, String>        nameColumn;
    @FXML private TableColumn<Equipment, String>        typeColumn;
    @FXML private TableColumn<Equipment, String>        statusColumn;
    @FXML private TableColumn<Equipment, LocalDateTime> purchaseDateColumn;
    @FXML private TableColumn<Equipment, Double>        priceColumn;
    @FXML private TableColumn<Equipment, String>        descriptionColumn;
    @FXML private TableColumn<Equipment, Long>          roomIdColumn;
    @FXML private TableColumn<Equipment, Void>          actionsColumn;

    // --- Buttons & loading indicator ---
    @FXML private Button            addButton;
    @FXML private Button            refreshButton;
    @FXML private Button            editButton;
    @FXML private Button            deleteButton;
    @FXML private ProgressIndicator progressIndicator;

    private final ObservableList<Equipment> equipmentList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTable();
        loadRooms();
        fetchData(ApiConfig.EQUIPMENTS);
    }

    private void setupTable() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        purchaseDateColumn.setCellValueFactory(new PropertyValueFactory<>("purchaseDate"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        roomIdColumn.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getRoom().getId())
        );

        actionsColumn.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = new Button("Edit");
            private final Button delBtn  = new Button("Delete");
            private final HBox  box      = new HBox(5, editBtn, delBtn);

            {
                editBtn.setOnAction(e -> openEquipmentForm(getCurrent()));
                delBtn.setOnAction (e -> deleteEquipment(getCurrent()));
            }
            private Equipment getCurrent() {
                return getTableView().getItems().get(getIndex());
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });

        equipmentTable.setItems(equipmentList);
        equipmentTable.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            boolean sel = n != null;
            editButton.setDisable(!sel);
            deleteButton.setDisable(!sel);
        });
    }

    private void loadRooms() {
        setLoading(true);
        Task<List<Room>> task = new Task<>() {
            @Override protected List<Room> call() throws Exception {
                HttpResponse<String> resp = ApiClient.getInstance().get(ApiConfig.ROOMS);
                if (resp.statusCode() == 200) {
                    return ApiClient.getInstance()
                            .getObjectMapper()
                            .readValue(resp.body(), new TypeReference<List<Room>>() {});
                }
                return List.of();
            }
        };
        task.setOnSucceeded(e -> {
            roomFilterCombo.setItems(FXCollections.observableArrayList(task.getValue()));
            setLoading(false);
        });
        task.setOnFailed(e -> setLoading(false));
        new Thread(task).start();
    }

    @FXML private void handleRefresh() {
        searchField.clear();
        roomFilterCombo.getSelectionModel().clearSelection();
        fetchData(ApiConfig.EQUIPMENTS);
    }

    @FXML private void handleSearch() {
        String q = searchField.getText().trim();
        Long roomId = roomFilterCombo.getValue() != null
                ? roomFilterCombo.getValue().getId()
                : null;
        String url;
        if (roomId != null) {
            url = ApiConfig.EQUIPMENTS + "/search?roomId=" + roomId;
        } else if (!q.isEmpty()) {
            try {
                url = ApiConfig.EQUIPMENTS + "/search?id=" + Long.parseLong(q);
            } catch (NumberFormatException ex) {
                String enc = URLEncoder.encode(q, StandardCharsets.UTF_8);
                url = ApiConfig.EQUIPMENTS + "/search?name=" + enc;
            }
        } else {
            url = ApiConfig.EQUIPMENTS;
        }
        fetchData(url);
    }

    @FXML private void handleClear() {
        searchField.clear();
        roomFilterCombo.getSelectionModel().clearSelection();
        fetchData(ApiConfig.EQUIPMENTS);
    }

    private void fetchData(String url) {
        setLoading(true);
        Task<List<Equipment>> task = new Task<>() {
            @Override protected List<Equipment> call() throws Exception {
                HttpResponse<String> resp = ApiClient.getInstance().get(url);
                if (resp.statusCode() == 200) {
                    return ApiClient.getInstance()
                            .getObjectMapper()
                            .readValue(resp.body(),
                                    new TypeReference<List<Equipment>>() {});
                }
                throw new RuntimeException("HTTP " + resp.statusCode());
            }
        };
        task.setOnSucceeded(e -> {
            equipmentList.setAll(task.getValue());
            setLoading(false);
        });
        task.setOnFailed(e -> {
            setLoading(false);
            showAlert("Error", "Cannot load data.");
        });
        new Thread(task).start();
    }

    @FXML private void handleAdd() {
        openEquipmentForm(null);
    }

    @FXML private void handleEdit() {
        Equipment sel = equipmentTable.getSelectionModel().getSelectedItem();
        if (sel != null) openEquipmentForm(sel);
    }

    @FXML private void handleDelete() {
        Equipment sel = equipmentTable.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        Alert c = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete this equipment?", ButtonType.OK, ButtonType.CANCEL);
        c.setHeaderText(null);
        c.showAndWait().filter(bt -> bt == ButtonType.OK)
                .ifPresent(bt -> deleteEquipment(sel));
    }

    private void deleteEquipment(Equipment eq) {
        setLoading(true);
        Task<Void> task = new Task<>() {
            @Override protected Void call() throws Exception {
                HttpResponse<String> resp = ApiClient.getInstance()
                        .delete(ApiConfig.EQUIPMENTS + "/" + eq.getId());
                if (resp.statusCode() == 200 || resp.statusCode() == 204) {
                    Platform.runLater(() -> {
                        equipmentList.remove(eq);
                        showAlert("Success", "Deleted successfully.", Alert.AlertType.INFORMATION);
                    });
                } else {
                    Platform.runLater(() -> showAlert("Error", "Failed to delete equipment."));
                }
                return null;
            }
            @Override protected void succeeded() { setLoading(false); }
            @Override protected void failed() {
                setLoading(false);
                showAlert("Error", "Connection failed. Please try again.");
            }
        };
        new Thread(task).start();
    }

    private void openEquipmentForm(Equipment equipment) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/Equipment/EquipmentForm.fxml")
            );
            Parent root = loader.load();  // <— load FXML first

            EquipmentFormController ctr = loader.getController();  // typed controller
            ctr.setEquipment(equipment);
            ctr.setParentController(this);

            Scene scene = new Scene(root);
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

    /** Called by form controller after save */
    public void onEquipmentSaved() {
        fetchData(ApiConfig.EQUIPMENTS);
    }

    private void setLoading(boolean loading) {
        Platform.runLater(() -> {
            progressIndicator.setVisible(loading);
            addButton    .setDisable(loading);
            editButton   .setDisable(loading);
            deleteButton .setDisable(loading);
            refreshButton.setDisable(loading);
            searchButton .setDisable(loading);
            clearButton  .setDisable(loading);
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