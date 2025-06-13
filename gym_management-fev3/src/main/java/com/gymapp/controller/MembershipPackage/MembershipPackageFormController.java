package com.gymapp.controller.MembershipPackage;

import com.gymapp.api.ApiClient;
import com.gymapp.api.ApiConfig;
import com.gymapp.model.MembershipPackage;
import com.gymapp.model.Member;
import com.gymapp.model.Package;
import com.gymapp.util.AlertHelper;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.List;

public class MembershipPackageFormController {

    @FXML private ComboBox<Member> memberComboBox;
    @FXML private ComboBox<Package> packageComboBox;
    @FXML private DatePicker startDatePicker;
    @FXML private Label endDateLabel;
    @FXML private Label priceLabel;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    @FXML
    public void initialize() {
        // Load danh sách hội viên
        new Thread(() -> {
            try {
                HttpResponse<String> resp = ApiClient.getInstance().get(ApiConfig.MEMBERS);
                List<Member> members = ApiClient.getInstance().getObjectMapper()
                        .readValue(resp.body(), new com.fasterxml.jackson.core.type.TypeReference<List<Member>>() {});
                Platform.runLater(() -> memberComboBox.setItems(FXCollections.observableArrayList(members)));
            } catch (Exception e) {
                Platform.runLater(() -> AlertHelper.showError("Error", "Không tải được danh sách hội viên"));
            }
        }).start();

        // Load danh sách gói tập
        new Thread(() -> {
            try {
                HttpResponse<String> resp = ApiClient.getInstance().get(ApiConfig.PACKAGES);
                List<Package> packages = ApiClient.getInstance().getObjectMapper()
                        .readValue(resp.body(), new com.fasterxml.jackson.core.type.TypeReference<List<Package>>() {});
                Platform.runLater(() -> packageComboBox.setItems(FXCollections.observableArrayList(packages)));
            } catch (Exception e) {
                Platform.runLater(() -> AlertHelper.showError("Error", "Không tải được danh sách gói tập"));
            }
        }).start();

        // Hiển thị tên hội viên trong ComboBox
        memberComboBox.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Member item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getFullName());
            }
        });
        memberComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Member item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getFullName());
            }
        });

        // Hiển thị tên gói tập trong ComboBox
        packageComboBox.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Package item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName());
            }
        });
        packageComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Package item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName());
            }
        });

        // Khi chọn gói tập hoặc ngày bắt đầu thì tính ngày kết thúc và giá
        packageComboBox.valueProperty().addListener((obs, oldVal, newVal) -> updateEndDateAndPrice());
        startDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> updateEndDateAndPrice());
    }

    private void updateEndDateAndPrice() {
        Package selectedPackage = packageComboBox.getValue();
        LocalDate startDate = startDatePicker.getValue();
        if (selectedPackage != null) {
            priceLabel.setText(String.format("%.0f VNĐ", selectedPackage.getPrice()));
            if (startDate != null && selectedPackage.getDurationMonths() != null) {
                LocalDate endDate = startDate.plusMonths(selectedPackage.getDurationMonths());
                endDateLabel.setText(endDate.toString());
            } else {
                endDateLabel.setText("");
            }
        } else {
            priceLabel.setText("");
            endDateLabel.setText("");
        }
    }

    @FXML
    private void handleSave() {
        Member member = memberComboBox.getValue();
        Package pack = packageComboBox.getValue();
        LocalDate startDate = startDatePicker.getValue();

        if (member == null || pack == null || startDate == null) {
            AlertHelper.showWarning("Thiếu thông tin", "Vui lòng chọn đầy đủ hội viên, gói tập và ngày bắt đầu.");
            return;
        }

        MembershipPackage mp = new MembershipPackage();
        mp.setMemberId(member.getId());
        mp.setMemberName(member.getFullName());
        mp.setPackageId(pack.getId());
        mp.setPackageName(pack.getName());
        mp.setPrice(pack.getPrice());
        mp.setStartDate(startDate);
        mp.setEndDate(startDate.plusMonths(pack.getDurationMonths()));


        new Thread(() -> {
            try {
                HttpResponse<String> resp = ApiClient.getInstance().post(ApiConfig.MEMBERSHIP_PACKAGES, mp);
                Platform.runLater(() -> {
                    if (resp.statusCode() == 200 || resp.statusCode() == 201) {
                        AlertHelper.showInfo("Thành công", "Đăng ký/gia hạn gói tập thành công!");
                        closeWindow();
                    } else {
                        AlertHelper.showError("Lỗi", "Không thể đăng ký/gia hạn gói tập.");
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> AlertHelper.showError("Lỗi", "Kết nối thất bại."));
            }
        }).start();
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}