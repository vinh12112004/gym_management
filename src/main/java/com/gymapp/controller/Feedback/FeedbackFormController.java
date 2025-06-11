package com.gymapp.controller.Feedback;

import com.gymapp.api.ApiClient;
import com.gymapp.api.ApiConfig;
import com.gymapp.model.Feedback;
import com.gymapp.util.AlertHelper;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.http.HttpResponse;

public class FeedbackFormController {

    @FXML private Label titleLabel;
    @FXML private TextField memberNameField;
    @FXML private TextField subjectField;
    @FXML private Spinner<Integer> ratingSpinner;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private TextArea messageArea;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;
    @FXML private ProgressIndicator progressIndicator;

    private Feedback feedback;
    private FeedbackController parentController;

    @FXML
    public void initialize() {
        ratingSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 5, 5));
        statusComboBox.setItems(FXCollections.observableArrayList("Pending", "Reviewed", "Resolved"));
        statusComboBox.setValue("Pending");
    }

    public void setFeedback(Feedback feedback) {
        this.feedback = feedback;
        if (feedback != null) {
            titleLabel.setText("Edit Feedback");
            memberNameField.setText(feedback.getMemberName());
            subjectField.setText(feedback.getSubject());
            ratingSpinner.getValueFactory().setValue(feedback.getRating());
            statusComboBox.setValue(feedback.getStatus());
            messageArea.setText(feedback.getMessage());
        }
    }

    public void setParentController(FeedbackController parentController) {
        this.parentController = parentController;
    }

    @FXML
    private void handleSave() {
        if (!validateInput()) return;

        setLoading(true);
        new Thread(new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Feedback toSave = createFeedbackFromForm();
                HttpResponse<String> response;
                if (feedback == null) {
                    response = ApiClient.getInstance()
                            .post(ApiConfig.FEEDBACK, toSave);
                } else {
                    toSave.setId(feedback.getId());
                    response = ApiClient.getInstance()
                            .put(ApiConfig.FEEDBACK + "/" + feedback.getId(), toSave);
                }

                if (response.statusCode() == 200 || response.statusCode() == 201) {
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

            @Override
            protected void succeeded() {
                setLoading(false);
            }
            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    setLoading(false);
                    AlertHelper.showError("Error", "Connection failed. Please try again.");
                });
            }
        }).start();
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private boolean validateInput() {
        if (memberNameField.getText().trim().isEmpty()) {
            AlertHelper.showWarning("Validation Error", "Member name is required.");
            return false;
        }
        if (subjectField.getText().trim().isEmpty()) {
            AlertHelper.showWarning("Validation Error", "Subject is required.");
            return false;
        }
        if (statusComboBox.getValue() == null) {
            AlertHelper.showWarning("Validation Error", "Status is required.");
            return false;
        }
        if (messageArea.getText().trim().isEmpty()) {
            AlertHelper.showWarning("Validation Error", "Message is required.");
            return false;
        }
        return true;
    }

    private Feedback createFeedbackFromForm() {
        Feedback f = new Feedback();
        f.setMemberName(memberNameField.getText().trim());
        f.setSubject(subjectField.getText().trim());
        f.setRating(ratingSpinner.getValue());
        f.setStatus(statusComboBox.getValue());
        f.setMessage(messageArea.getText().trim());
        return f;
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