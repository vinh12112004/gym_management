package com.gymapp.controller.Room;

import com.fasterxml.jackson.core.type.TypeReference;
import com.gymapp.api.ApiClient;
import com.gymapp.api.ApiConfig;
import com.gymapp.model.Room;
import com.gymapp.util.AlertHelper;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.ResourceBundle;

public class RoomController implements Initializable {
    @FXML private TableView<Room> roomTable;
    @FXML private TableColumn<Room, Long>   idColumn;
    @FXML private TableColumn<Room, String> nameColumn;
    @FXML private TableColumn<Room, String> roomTypeColumn;
    @FXML private TableColumn<Room, String> addressColumn;
    @FXML private TableColumn<Room, String> openTimeColumn;
    @FXML private TableColumn<Room, String> closeTimeColumn;
    @FXML private TableColumn<Room, String> roomStatusColumn;
    /** Cột mới để hiển thị số thiết bị trong mỗi phòng */
    @FXML private TableColumn<Room, Integer> equipmentCountColumn;

    @FXML private Button addButton;
    @FXML private Button editButton;
    @FXML private Button deleteButton;
    @FXML private Button refreshButton;
    @FXML private ProgressIndicator progressIndicator;

    private final ObservableList<Room> roomList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTable();
        refreshData();
    }

    private void setupTable() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        roomTypeColumn.setCellValueFactory(new PropertyValueFactory<>("roomType"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        openTimeColumn.setCellValueFactory(new PropertyValueFactory<>("openTime"));
        closeTimeColumn.setCellValueFactory(new PropertyValueFactory<>("closeTime"));
        roomStatusColumn.setCellValueFactory(new PropertyValueFactory<>("roomStatus"));
        // Thiết lập cell factory cho cột đếm equipmentCount
        equipmentCountColumn.setCellValueFactory(
                new PropertyValueFactory<>("equipmentCount")
        );

        roomTable.setItems(roomList);
        roomTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            boolean has = newSel != null;
            editButton.setDisable(!has);
            deleteButton.setDisable(!has);
        });
    }

    @FXML
    private void refreshData() {
        setLoading(true);
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                HttpResponse<String> response = ApiClient.getInstance().get(ApiConfig.ROOMS);

                if (response.statusCode() == 200) {
                    List<Room> list = ApiClient.getInstance()
                            .getObjectMapper()
                            .readValue(response.body(), new TypeReference<List<Room>>() {});
                    Platform.runLater(() -> roomList.setAll(list));
                } else {
                    Platform.runLater(() ->
                            AlertHelper.showError("Error", "Failed to load room data.")
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

    @FXML private void handleAdd()  { openRoomForm(null); }
    @FXML private void handleEdit() {
        Room sel = roomTable.getSelectionModel().getSelectedItem();
        if (sel != null) openRoomForm(sel);
    }
    @FXML private void handleDelete() {
        Room sel = roomTable.getSelectionModel().getSelectedItem();
        if (sel != null && AlertHelper.showConfirmation(
                "Confirm Delete",
                "Are you sure you want to delete this room?")) {
            deleteRoom(sel);
        }
    }

    private void deleteRoom(Room room) {
        setLoading(true);
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                HttpResponse<String> resp = ApiClient.getInstance()
                        .delete(ApiConfig.ROOMS + "/" + room.getId());

                if (resp.statusCode() == 200 || resp.statusCode() == 204) {
                    Platform.runLater(() -> {
                        roomList.remove(room);
                        AlertHelper.showInfo("Success", "Room deleted successfully.");
                    });
                } else {
                    Platform.runLater(() ->
                            AlertHelper.showError("Error", "Failed to delete room.")
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

    private void openRoomForm(Room room) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/Room/RoomForm.fxml")
            );
            Scene scene = new Scene(loader.load(), 450, 500);
            RoomFormController ctrl = loader.getController();
            ctrl.setRoom(room);
            ctrl.setParentController(this);

            Stage stage = new Stage();
            stage.setTitle(room == null ? "Add Room" : "Edit Room");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            AlertHelper.showError("Error", "Could not load room form.");
        }
    }

    public void onRoomSaved() {
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