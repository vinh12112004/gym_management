package com.gymapp.controller.MembershipPackage;

import com.gymapp.api.ApiClient;
import com.gymapp.api.ApiConfig;
import com.gymapp.model.MembershipPackage;
import com.gymapp.util.AlertHelper;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.http.HttpResponse;
import java.util.List;

public class MembershipPackageController {

    @FXML private TableView<MembershipPackage> membershipPackageTable;
    @FXML private TableColumn<MembershipPackage, String> memberNameColumn;
    @FXML private TableColumn<MembershipPackage, String> packageNameColumn;
    @FXML private TableColumn<MembershipPackage, String> startDateColumn;
    @FXML private TableColumn<MembershipPackage, String> endDateColumn;
    @FXML private TableColumn<MembershipPackage, Double> priceColumn;
    @FXML private Button addButton;
    @FXML private Button refreshButton;

    private final ObservableList<MembershipPackage> membershipPackageList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        memberNameColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getMemberName()));
        packageNameColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getPackageName()));
        startDateColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStartDate().toString()));
        endDateColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEndDate().toString()));
        priceColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getPrice()));
        membershipPackageTable.setItems(membershipPackageList);

        refreshData();
    }

    @FXML
    public void handleAdd() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MembershipPackage/MembershipPackageForm.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Đăng ký/Gia hạn gói tập");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            refreshData();
        } catch (Exception e) {
            AlertHelper.showError("Lỗi", "Không mở được form đăng ký/gia hạn.");
        }
    }

    @FXML
    public void refreshData() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                HttpResponse<String> resp = ApiClient.getInstance().get(ApiConfig.MEMBERSHIP_PACKAGES);
                if (resp.statusCode() == 200) {
                    List<MembershipPackage> list = ApiClient.getInstance().getObjectMapper()
                            .readValue(resp.body(), new com.fasterxml.jackson.core.type.TypeReference<List<MembershipPackage>>() {});
                    Platform.runLater(() -> {
                        membershipPackageList.clear();
                        membershipPackageList.addAll(list);
                    });
                } else {
                    Platform.runLater(() -> AlertHelper.showError("Lỗi", "Không tải được danh sách đăng ký gói tập."));
                }
                return null;
            }
        };
        new Thread(task).start();
    }
}