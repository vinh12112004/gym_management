package com.gymapp.controller.MemberPackage;

import com.gymapp.api.ApiClient;
import com.gymapp.api.ApiConfig;
import com.gymapp.model.MembershipPackage;
import com.gymapp.model.Staff;
import com.gymapp.util.SessionManager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.net.http.HttpResponse;
import java.util.List;

public class MemberPackagesController {

    @FXML private TableView<MembershipPackage> membershipPackageTable;
    @FXML private TableColumn<MembershipPackage, String> packageNameColumn;
    @FXML private TableColumn<MembershipPackage, String> startDateColumn;
    @FXML private TableColumn<MembershipPackage, String> endDateColumn;
    @FXML private TableColumn<MembershipPackage, Double> priceColumn;
    @FXML private TableColumn<MembershipPackage, String> coachNameColumn; // Thêm dòng này

    private final ObservableList<MembershipPackage> membershipPackageList = FXCollections.observableArrayList();
    private List<Staff> staffList; // Thêm dòng này

    @FXML
    public void initialize() {
        packageNameColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getPackageName()));
        startDateColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStartDate().toString()));
        endDateColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEndDate().toString()));
        priceColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getPrice()));
        membershipPackageTable.setItems(membershipPackageList);

        loadStaffAndMemberPackages(); // Đổi tên hàm để load staff trước
    }

    private void loadStaffAndMemberPackages() {
        // Load staff trước
        new Thread(() -> {
            try {
                HttpResponse<String> staffResp = ApiClient.getInstance().get(ApiConfig.STAFFS);
                if (staffResp.statusCode() == 200) {
                    staffList = ApiClient.getInstance().getObjectMapper()
                            .readValue(staffResp.body(), new com.fasterxml.jackson.core.type.TypeReference<List<Staff>>() {});
                }
            } catch (Exception e) {
                staffList = null;
            }
            // Sau khi load staff xong, load package
            loadMemberPackages();
        }).start();
    }

    private void loadMemberPackages() {
        String email = SessionManager.getInstance().getEmail();
        if (email == null || email.isEmpty()) {
            return;
        }

        new Thread(() -> {
            try {
                HttpResponse<String> resp = ApiClient.getInstance().get(ApiConfig.MEMBERS + "/email/" + email);
                if (resp.statusCode() == 200) {
                    com.fasterxml.jackson.databind.JsonNode node = ApiClient.getInstance().getObjectMapper().readTree(resp.body());
                    Long memberId = node.get("id").asLong();

                    String pkgUrl = ApiConfig.MEMBERSHIP_PACKAGES + "/member/" + memberId;
                    HttpResponse<String> pkgResp = ApiClient.getInstance().get(pkgUrl);
                    if (pkgResp.statusCode() == 200) {
                        List<MembershipPackage> list = ApiClient.getInstance().getObjectMapper()
                                .readValue(pkgResp.body(), new com.fasterxml.jackson.core.type.TypeReference<List<MembershipPackage>>() {});
                        Platform.runLater(() -> {
                            membershipPackageList.clear();
                            membershipPackageList.addAll(list);

                            // Map tên huấn luyện viên cho từng dòng
                            coachNameColumn.setCellValueFactory(cellData -> {
                                if (cellData.getValue().getCoach() == null || staffList == null) {
                                    return new javafx.beans.property.SimpleStringProperty("");
                                }
                                Long coachId = cellData.getValue().getCoach().getId();
                                String name = staffList.stream()
                                        .filter(s -> s.getId().equals(coachId))
                                        .map(s -> s.getFirstName() + " " + s.getLastName())
                                        .findFirst().orElse("");
                                return new javafx.beans.property.SimpleStringProperty(name);
                            });
                        });
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}