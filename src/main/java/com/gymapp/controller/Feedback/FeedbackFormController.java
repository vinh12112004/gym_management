package com.gymapp.controller.Feedback;

import com.gymapp.api.ApiClient;
import com.gymapp.api.ApiConfig;
import com.gymapp.model.Feedback;
import com.gymapp.util.AlertHelper;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.ResourceBundle;

public class FeedbackFormController implements Initializable {

    @FXML private Label titleLabel;
    @FXML private TextField subjectField;
    @FXML private Spinner<Integer> ratingSpinner;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private TextArea messageArea;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;
    @FXML private ProgressIndicator progressIndicator;

    private Feedback feedback;
    private FeedbackController parentController;  // Kèm import phía dưới

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Thiết lập spinner từ 1–5, mặc định 5
        ratingSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 5, 5)
        );
        // Thiết lập ComboBox cho status
        statusComboBox.setItems(FXCollections.observableArrayList(
                "Pending", "Reviewed", "Resolved"
        ));
        statusComboBox.setValue("Pending");

        progressIndicator.setVisible(false);
    }

    /**
     * Gọi từ FeedbackController khi mở form để CHỈNH SỬA
     */
    public void setFeedback(Feedback feedback) {
        this.feedback = feedback;
        if (feedback != null) {
            titleLabel.setText("Edit Feedback");
            subjectField.setText(feedback.getSubject());
            ratingSpinner.getValueFactory().setValue(feedback.getRating());
            statusComboBox.setValue(feedback.getStatus());
            messageArea.setText(feedback.getMessage());
        }
    }

    /**
     * Gọi từ FeedbackController để gán controller cha,
     * phục vụ việc gọi parentController.refreshData()
     */
    public void setParentController(FeedbackController parentController) {
        this.parentController = parentController;
    }

    @FXML
    private void handleSave() {
        if (!validateInput()) return;
        setLoading(true);

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                // Chỉ gửi payload với các trường cần thiết
                Map<String,Object> payload = Map.of(
                        "subject", subjectField.getText().trim(),
                        "rating", ratingSpinner.getValue(),
                        "status", statusComboBox.getValue(),
                        "message", messageArea.getText().trim()
                );

                HttpResponse<String> resp;
                if (feedback == null) {
                    // Tạo mới
                    resp = ApiClient.getInstance().post(ApiConfig.FEEDBACK, payload);
                } else {
                    // Cập nhật
                    resp = ApiClient.getInstance()
                            .put(ApiConfig.FEEDBACK + "/" + feedback.getId(), payload);
                }

                if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
                    Platform.runLater(() -> {
                        AlertHelper.showInfo("Success", "Feedback saved successfully.");
                        if (parentController != null) {
                            parentController.refreshData();
                        }
                        closeWindow();
                    });
                } else {
                    Platform.runLater(() ->
                            AlertHelper.showError("Error", "Failed to save feedback.")
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

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private boolean validateInput() {
        if (subjectField.getText().trim().isEmpty()) {
            AlertHelper.showWarning("Validation Error", "Subject is required.");
            return false;
        }
        if (messageArea.getText().trim().isEmpty()) {
            AlertHelper.showWarning("Validation Error", "Message is required.");
            return false;
        }
        return true;
    }

    private void setLoading(boolean loading) {
        Platform.runLater(() -> {
            progressIndicator.setVisible(loading);
            saveButton.setDisable(loading);
            cancelButton.setDisable(loading);
        });
    }

    private void closeWindow() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}