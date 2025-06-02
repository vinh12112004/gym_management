package com.example.ITSS.controller;

import java.util.List;

import com.example.ITSS.UserSession;
import com.example.ITSS.api.FeedbackApi;
import com.example.ITSS.model.Feedback;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class FeedbackController {
    @FXML private TableView<Feedback> tableFeedback;
    @FXML private TableColumn<Feedback, Integer> colFeedbackId;
    @FXML private TableColumn<Feedback, Integer> colUserId;
    @FXML private TableColumn<Feedback, String> colTargetType;
    @FXML private TableColumn<Feedback, Integer> colTargetId;
    @FXML private TableColumn<Feedback, String> colContent;
    @FXML private TableColumn<Feedback, Integer> colRating;
    @FXML private TableColumn<Feedback, String> colCreatedAt;

    @FXML private TextField txtFeedbackId;
    @FXML private TextField txtUserId;
    @FXML private TextField txtTargetType;
    @FXML private TextField txtTargetId;
    @FXML private TextArea txtContent;
    @FXML private Spinner<Integer> spnRating;
    @FXML private TextField txtCreatedAt;

    @FXML
    public void initialize() {
        colFeedbackId.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getId()).asObject());
        colUserId.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getUserId()).asObject());
        colTargetType.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getTargetType()));
        colTargetId.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getTargetId()).asObject());
        colContent.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getContent()));
        colRating.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getRating()).asObject());
        colCreatedAt.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getCreatedAt()));

        spnRating.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 5, 5));
        // Set userId từ session và chỉ đọc
        txtUserId.setText(String.valueOf(UserSession.getId()));
        txtUserId.setEditable(false);

        loadFeedbacks();
    }

    private void loadFeedbacks() {
        try {
            List<Feedback> list = FeedbackApi.getAllFeedbacks();
            ObservableList<Feedback> data = FXCollections.observableArrayList(list);
            tableFeedback.setItems(data);
        } catch (Exception e) {
            showAlert("Load feedbacks failed: " + e.getMessage());
        }
    }

    @FXML
    private void clearForm() {
        txtTargetType.clear();
        txtTargetId.clear();
        txtContent.clear();
        spnRating.getValueFactory().setValue(5);
    }

    @FXML
    private void sendFeedback() {
        try {
            int userId = Integer.parseInt(txtUserId.getText());
            String targetType = txtTargetType.getText();
            int targetId = Integer.parseInt(txtTargetId.getText());
            String content = txtContent.getText();
            int rating = spnRating.getValue();

            Feedback fb = new Feedback(0, userId, targetType, targetId, content, rating, null);
            FeedbackApi.sendFeedback(fb);
            showAlertInfo("Feedback sent!");
            loadFeedbacks();
            clearForm();
        } catch (Exception e) {
            showAlert("Send failed: " + e.getMessage());
        }
    }

    @FXML
    private void showDashboard() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/Dashboard.fxml"));
            javafx.scene.Parent root = loader.load();
            javafx.stage.Stage stage = (javafx.stage.Stage) tableFeedback.getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(root));
            stage.setTitle("Dashboard");
        } catch (Exception e) {
            showAlert("Cannot open Dashboard: " + e.getMessage());
        }
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        alert.showAndWait();
    }
    private void showAlertInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        alert.showAndWait();
    }
}