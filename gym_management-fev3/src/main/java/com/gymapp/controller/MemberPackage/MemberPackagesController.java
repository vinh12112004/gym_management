package com.gymapp.controller.MemberPackage;

import com.gymapp.api.ApiClient;
import com.gymapp.api.ApiConfig;
import com.gymapp.model.MembershipPackage;
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

    private final ObservableList<MembershipPackage> membershipPackageList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        packageNameColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getPackageName()));
        startDateColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStartDate().toString()));
        endDateColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEndDate().toString()));
        priceColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getPrice()));
        membershipPackageTable.setItems(membershipPackageList);

        loadMemberPackages();
    }

    private void loadMemberPackages() {
        String email = SessionManager.getInstance().getEmail();
        System.out.println("[DEBUG] Email: " + email);
        if (email == null || email.isEmpty()) {
            System.out.println("[DEBUG] Email is null or empty, aborting loadMemberPackages.");
            return;
        }

        new Thread(() -> {
            try {
                System.out.println("[DEBUG] Calling API: " + ApiConfig.MEMBERS + "/email/" + email);
                HttpResponse<String> resp = ApiClient.getInstance().get(ApiConfig.MEMBERS + "/email/" + email);
                System.out.println("[DEBUG] Member API status: " + resp.statusCode());
                System.out.println("[DEBUG] Member API body: " + resp.body());
                if (resp.statusCode() == 200) {
                    com.fasterxml.jackson.databind.JsonNode node = ApiClient.getInstance().getObjectMapper().readTree(resp.body());
                    Long memberId = node.get("id").asLong();
                    System.out.println("[DEBUG] memberId: " + memberId);

                    String pkgUrl = ApiConfig.MEMBERSHIP_PACKAGES + "/member/" + memberId;
                    System.out.println("[DEBUG] Calling API: " + pkgUrl);
                    HttpResponse<String> pkgResp = ApiClient.getInstance().get(pkgUrl);
                    System.out.println("[DEBUG] Packages API status: " + pkgResp.statusCode());
                    System.out.println("[DEBUG] Packages JSON: " + pkgResp.body());
                    if (pkgResp.statusCode() == 200) {
                        List<MembershipPackage> list = ApiClient.getInstance().getObjectMapper()
                                .readValue(pkgResp.body(), new com.fasterxml.jackson.core.type.TypeReference<List<MembershipPackage>>() {});
                        System.out.println("[DEBUG] Loaded packages: " + list.size());
                        for (MembershipPackage mp : list) {
                            System.out.println("[DEBUG] Package: " + mp.getPackageName() + " | " + mp.getStartDate() + " - " + mp.getEndDate() + " | " + mp.getPrice());
                        }
                        Platform.runLater(() -> {
                            membershipPackageList.clear();
                            membershipPackageList.addAll(list);
                            System.out.println("[DEBUG] membershipPackageList size after addAll: " + membershipPackageList.size());
                        });
                    }
                }

            } catch (Exception e) {
                System.out.println("[DEBUG] Exception in loadMemberPackages:");
                e.printStackTrace();
            }
        }).start();
    }
}