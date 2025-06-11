package com.gymapp.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.gymapp.api.ApiClient;
import com.gymapp.api.ApiConfig;
import com.gymapp.model.Package;
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

public class PackageController implements Initializable {

    @FXML private TableView<Package> packageTable;
    @FXML private TableColumn<Package, Long> idColumn;
    @FXML private TableColumn<Package, String> nameColumn;
    @FXML private TableColumn<Package, Double> priceColumn;
    @FXML private TableColumn<Package, Integer> durationColumn;
    @FXML private TableColumn<Package, String> statusColumn;
    @FXML private TableColumn<Package, String> descriptionColumn;
    @FXML private Button addButton;
    @FXML private Button editButton;
    @FXML private Button deleteButton;
    @FXML private Button refreshButton;
    @FXML private ProgressIndicator progressIndicator;

    private final ObservableList<Package> packageList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTable();
        refreshData();
    }

    private void setupTable() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        durationColumn.setCellValueFactory(new PropertyValueFactory<>("durationMonths"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        packageTable.setItems(packageList);
        packageTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            boolean sel = newSel != null;
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
                // ✅ Dùng hằng số số nhiều
                HttpResponse<String> response = ApiClient.getInstance()
                        .get(ApiConfig.PACKAGES);

                if (response.statusCode() == 200) {
                    List<Package> list = ApiClient.getInstance()
                            .getObjectMapper()
                            .readValue(response.body(), new TypeReference<List<Package>>() {});
                    Platform.runLater(() -> {
                        packageList.setAll(list);
                    });
                } else {
                    Platform.runLater(() ->
                            AlertHelper.showError("Error", "Failed to load package data.")
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

    @FXML private void handleAdd()  { openPackageForm(null); }
    @FXML private void handleEdit() {
        Package sel = packageTable.getSelectionModel().getSelectedItem();
        if (sel != null) openPackageForm(sel);
    }
    @FXML private void handleDelete() {
        Package sel = packageTable.getSelectionModel().getSelectedItem();
        if (sel != null && AlertHelper.showConfirmation(
                "Confirm Delete",
                "Are you sure you want to delete this package?")) {
            deletePackage(sel);
        }
    }

    private void deletePackage(Package pkg) {
        setLoading(true);
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                // ✅ Dùng hằng số số nhiều
                HttpResponse<String> resp = ApiClient.getInstance()
                        .delete(ApiConfig.PACKAGES + "/" + pkg.getId());

                if (resp.statusCode() == 200 || resp.statusCode() == 204) {
                    Platform.runLater(() -> {
                        packageList.remove(pkg);
                        AlertHelper.showInfo("Success", "Package deleted successfully.");
                    });
                } else {
                    Platform.runLater(() ->
                            AlertHelper.showError("Error", "Failed to delete package.")
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

    private void openPackageForm(Package pkg) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PackageForm.fxml"));
            Scene scene = new Scene(loader.load(), 500, 600);
            PackageFormController ctr = loader.getController();
            ctr.setPackage(pkg);
            ctr.setParentController(this);

            Stage stage = new Stage();
            stage.setTitle(pkg == null ? "Add Package" : "Edit Package");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            AlertHelper.showError("Error", "Could not load package form.");
        }
    }

    public void onPackageSaved() {
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
