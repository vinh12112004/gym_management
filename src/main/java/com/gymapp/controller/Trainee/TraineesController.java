package com.gymapp.controller.Trainee;

import com.gymapp.api.ApiClient;
import com.gymapp.api.ApiConfig;
import com.gymapp.model.MembershipPackage;
import com.gymapp.model.Member;
import com.gymapp.model.Package;
import com.gymapp.util.SessionManager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.net.http.HttpResponse;
import java.util.List;

public class TraineesController {

    @FXML private TableView<MembershipPackage> traineeTable;
    @FXML private TableColumn<MembershipPackage, String> nameColumn;
    @FXML private TableColumn<MembershipPackage, String> emailColumn;
    @FXML private TableColumn<MembershipPackage, String> phoneColumn;
    @FXML private TableColumn<MembershipPackage, String> packageColumn;
    @FXML private TableColumn<MembershipPackage, String> startDateColumn;
    @FXML private TableColumn<MembershipPackage, String> endDateColumn;

    private final ObservableList<MembershipPackage> traineeList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        traineeTable.setItems(traineeList);

        nameColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getMember() != null ? cellData.getValue().getMember().getFullName() : ""
        ));
        emailColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getMember() != null ? cellData.getValue().getMember().getEmail() : ""
        ));
        phoneColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getMember() != null ? cellData.getValue().getMember().getPhone() : ""
        ));
        packageColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getAPackage() != null ? cellData.getValue().getAPackage().getName() : ""
        ));
        startDateColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getStartDate() != null ? cellData.getValue().getStartDate().toString() : ""
        ));
        endDateColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getEndDate() != null ? cellData.getValue().getEndDate().toString() : ""
        ));

        loadTrainees();
    }

    private void loadTrainees() {
        String email = SessionManager.getInstance().getEmail();
        if (email == null || email.isEmpty()) return;

        new Thread(() -> {
            try {
                // 1. Lấy staff theo email
                String staffUrl = ApiConfig.STAFFS + "/email/" + email;
                HttpResponse<String> staffResp = ApiClient.getInstance().get(staffUrl);
                if (staffResp.statusCode() == 200) {
                    com.fasterxml.jackson.databind.JsonNode node = ApiClient.getInstance().getObjectMapper().readTree(staffResp.body());
                    Long coachId = node.get("id").asLong();

                    // 2. Lấy danh sách MembershipPackage theo coachId
                    String url = ApiConfig.MEMBERSHIP_PACKAGES + "/coach/" + coachId;
                    HttpResponse<String> resp = ApiClient.getInstance().get(url);
                    if (resp.statusCode() == 200) {
                        List<MembershipPackage> list = ApiClient.getInstance().getObjectMapper()
                                .readValue(resp.body(), new com.fasterxml.jackson.core.type.TypeReference<List<MembershipPackage>>() {});
                        Platform.runLater(() -> {
                            traineeList.clear();
                            traineeList.addAll(list);
                        });
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}