package com.gymapp.controller.WorkoutHistory;

import com.gymapp.api.ApiClient;
import com.gymapp.api.ApiConfig;
import com.gymapp.model.Member;
import com.gymapp.model.WorkoutHistory;
import com.gymapp.util.SessionManager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class WorkoutHistoryController {

    @FXML private ComboBox<Member> memberComboBox;
    @FXML private HBox memberSelectionBox;
    @FXML private TableView<WorkoutHistory> historyTable;
    @FXML private TableColumn<WorkoutHistory, String> dateColumn;
    @FXML private TableColumn<WorkoutHistory, String> noteColumn;
    @FXML private DatePicker datePicker;
    @FXML private TextField noteField;

    private final ObservableList<WorkoutHistory> historyList = FXCollections.observableArrayList();
    private final ObservableList<Member> memberList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        historyTable.setItems(historyList);
        dateColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getWorkoutDate() != null ? cellData.getValue().getWorkoutDate().toString() : ""
        ));
        noteColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getNote() != null ? cellData.getValue().getNote() : ""
        ));

        memberComboBox.setItems(memberList);
        memberComboBox.setConverter(new javafx.util.StringConverter<>() {
            @Override
            public String toString(Member member) {
                return member == null ? "" : member.getFullName() + " (" + member.getEmail() + ")";
            }
            @Override
            public Member fromString(String s) { return null; }
        });

        memberComboBox.valueProperty().addListener((obs, oldVal, newVal) -> loadHistory());

        // Kiểm tra role của user
        String userRole = SessionManager.getInstance().getCurrentRole();
        if ("MEMBER".equals(userRole)) {
            // Ẩn cả HBox chứa ComboBox
            memberSelectionBox.setVisible(false);
            memberSelectionBox.setManaged(false);

            // Load thông tin member hiện tại
            loadCurrentMember();
        } else {
            // Nếu là admin/staff thì load tất cả members
            loadMembers();
        }
    }

    private void loadCurrentMember() {
        new Thread(() -> {
            try {
                // Lấy email từ SessionManager
                String currentEmail = SessionManager.getInstance().getEmail();
                if (currentEmail == null || currentEmail.isEmpty()) {
                    System.err.println("Không có email trong session");
                    return;
                }
                
                // Tìm member theo email
                HttpResponse<String> resp = ApiClient.getInstance().get(ApiConfig.MEMBERS);
                if (resp.statusCode() == 200) {
                    List<Member> allMembers = ApiClient.getInstance().getObjectMapper()
                            .readValue(resp.body(), new com.fasterxml.jackson.core.type.TypeReference<List<Member>>() {});
                    
                    // Tìm member có email trùng khớp
                    Member currentMember = allMembers.stream()
                            .filter(member -> currentEmail.equals(member.getEmail()))
                            .findFirst()
                            .orElse(null);
                    
                    if (currentMember != null) {
                        Platform.runLater(() -> {
                            memberList.clear();
                            memberList.add(currentMember);
                            memberComboBox.getSelectionModel().selectFirst();
                            loadHistory();
                        });
                    } else {
                        System.err.println("Không tìm thấy member với email: " + currentEmail);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void loadMembers() {
        new Thread(() -> {
            try {
                HttpResponse<String> resp = ApiClient.getInstance().get(ApiConfig.MEMBERS);
                if (resp.statusCode() == 200) {
                    List<Member> list = ApiClient.getInstance().getObjectMapper()
                            .readValue(resp.body(), new com.fasterxml.jackson.core.type.TypeReference<List<Member>>() {});
                    Platform.runLater(() -> {
                        memberList.clear();
                        memberList.addAll(list);
                        if (!memberList.isEmpty()) memberComboBox.getSelectionModel().selectFirst();
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void loadHistory() {
        Member selected = memberComboBox.getValue();
        if (selected == null) {
            historyList.clear();
            return;
        }
        Long memberId = selected.getId();

        new Thread(() -> {
            try {
                String url = ApiConfig.WORKOUT_HISTORY + "/member/" + memberId;
                HttpResponse<String> resp = ApiClient.getInstance().get(url);
                if (resp.statusCode() == 200) {
                    List<WorkoutHistory> list = ApiClient.getInstance().getObjectMapper()
                            .readValue(resp.body(), new com.fasterxml.jackson.core.type.TypeReference<List<WorkoutHistory>>() {});
                    Platform.runLater(() -> {
                        historyList.clear();
                        historyList.addAll(list);
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    @FXML
    private void handleAdd() {
        LocalDate date = datePicker.getValue();
        String note = noteField.getText();
        Member selected = memberComboBox.getValue();
        if (date == null || note == null || note.isEmpty() || selected == null) return;

        new Thread(() -> {
            try {
                var mapper = ApiClient.getInstance().getObjectMapper();
                var root = mapper.createObjectNode();

                root.put("memberId", selected.getId());
                root.put("note", note);
                root.put("workoutDate", date.toString());

                String json = mapper.writeValueAsString(root);
                System.out.println("[DEBUG] JSON gửi đi: " + json);

                // Sử dụng postWithoutCheck thay vì post
                HttpResponse<String> resp = ApiClient.getInstance().postWithoutCheck("/workout_history", root);

                System.out.println("[DEBUG] Response Status: " + resp.statusCode());
                System.out.println("[DEBUG] Response Body: " + resp.body());

                if (resp.statusCode() == 200 || resp.statusCode() == 201) {
                    Platform.runLater(() -> {
                        noteField.clear();
                        datePicker.setValue(null);
                        loadHistory();
                        showAlert("Thành công", "Đã thêm workout history!", Alert.AlertType.INFORMATION);
                    });
                } else {
                    System.err.println("Lỗi khi POST: " + resp.statusCode() + " - " + resp.body());
                    Platform.runLater(() -> {
                        showAlert("Lỗi", "Không thể thêm workout history: " + resp.body(), Alert.AlertType.ERROR);
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    showAlert("Lỗi", "Có lỗi xảy ra: " + e.getMessage(), Alert.AlertType.ERROR);
                });
            }
        }).start();
    }

    // Thêm method để hiện thông báo
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}