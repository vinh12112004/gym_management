package com.gymapp.controller.MembershipPackage;

import com.gymapp.api.ApiClient;
import com.gymapp.api.ApiConfig;
import com.gymapp.model.MembershipPackage;
import com.gymapp.model.Member;
import com.gymapp.model.Package;
import com.gymapp.model.Staff;
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
    @FXML private TableColumn<MembershipPackage, String> coachNameColumn;

    private final ObservableList<MembershipPackage> membershipPackageList = FXCollections.observableArrayList();
    
    // Danh sách để map tên
    private List<Member> memberList;
    private List<Package> packageList;
    private List<Staff> staffList;

    @FXML
    public void initialize() {
        loadMembersAndPackages();

        startDateColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getStartDate() != null ? cellData.getValue().getStartDate().toString() : ""
        ));
        endDateColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getEndDate() != null ? cellData.getValue().getEndDate().toString() : ""
        ));
        priceColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getPrice()));
        membershipPackageTable.setItems(membershipPackageList);
    }

    private void loadMembersAndPackages() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                try {
                    // Load members
                    HttpResponse<String> memberResp = ApiClient.getInstance().get(ApiConfig.MEMBERS);
                    memberList = ApiClient.getInstance().getObjectMapper()
                            .readValue(memberResp.body(), new com.fasterxml.jackson.core.type.TypeReference<List<Member>>() {});

                    // Load packages
                    HttpResponse<String> packageResp = ApiClient.getInstance().get(ApiConfig.PACKAGES);
                    packageList = ApiClient.getInstance().getObjectMapper()
                            .readValue(packageResp.body(), new com.fasterxml.jackson.core.type.TypeReference<List<Package>>() {});
                    // Load staff
                    HttpResponse<String> staffResp = ApiClient.getInstance().get(ApiConfig.STAFFS);
                    staffList = ApiClient.getInstance().getObjectMapper()
                            .readValue(staffResp.body(), new com.fasterxml.jackson.core.type.TypeReference<List<Staff>>() {});
                    Platform.runLater(() -> {
                        // Map tên hội viên
                        memberNameColumn.setCellValueFactory(cellData -> {
                            Long memberId = cellData.getValue().getMember() != null ? cellData.getValue().getMember().getId() : null;
                            String name = "";
                            if (memberId != null && memberList != null) {
                                name = memberList.stream()
                                        .filter(m -> m.getId().equals(memberId))
                                        .map(m -> m.getFirstName() + " " + m.getLastName())
                                        .findFirst().orElse("");
                            }
                            return new javafx.beans.property.SimpleStringProperty(name);
                        });

                        // Map tên gói tập
                        packageNameColumn.setCellValueFactory(cellData -> {
                            Long packageId = cellData.getValue().getAPackage() != null ? cellData.getValue().getAPackage().getId() : null;
                            String name = "";
                            if (packageId != null && packageList != null) {
                                name = packageList.stream()
                                        .filter(p -> p.getId().equals(packageId))
                                        .map(Package::getName)
                                        .findFirst().orElse("");
                            }
                            return new javafx.beans.property.SimpleStringProperty(name);
                        });

                        // Map tên huấn luyện viên
                        coachNameColumn.setCellValueFactory(cellData -> {
                            Long coachId = cellData.getValue().getCoach() != null ? cellData.getValue().getCoach().getId() : null;
                            String name = "";
                            if (coachId != null && staffList != null) {
                                name = staffList.stream()
                                        .filter(s -> s.getId().equals(coachId))
                                        .map(s -> s.getFirstName() + " " + s.getLastName())
                                        .findFirst().orElse("");
                            }
                            return new javafx.beans.property.SimpleStringProperty(name);
                        });

                        refreshData();
                    });
                } catch (Exception e) {
                    Platform.runLater(() -> AlertHelper.showError("Lỗi", "Không tải được danh sách hội viên hoặc gói tập."));
                }
                return null;
            }
        };
        new Thread(task).start();
    }

    @FXML
    public void handleAdd() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MembershipPackage/MembershipPackageForm.fxml"));
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Đăng ký/Gia hạn gói tập");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            refreshData();
        } catch (Exception e) {
            e.printStackTrace();
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