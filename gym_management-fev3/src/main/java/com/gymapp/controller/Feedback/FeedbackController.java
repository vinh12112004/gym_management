package com.gymapp.controller.Feedback;

import com.fasterxml.jackson.core.type.TypeReference;
import com.gymapp.api.ApiClient;
import com.gymapp.api.ApiConfig;
import com.gymapp.model.Feedback;
import com.gymapp.util.AlertHelper;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class FeedbackController implements Initializable {

    @FXML private TableView<Feedback> feedbackTable;
    @FXML private TableColumn<Feedback, Long> idColumn;
    @FXML private TableColumn<Feedback, String> memberColumn;
    @FXML private TableColumn<Feedback, String> subjectColumn;
    @FXML private TableColumn<Feedback, Integer> ratingColumn;
    @FXML private TableColumn<Feedback, String> statusColumn;

    // 1. Đổi kiểu cột ngày
    @FXML private TableColumn<Feedback, LocalDateTime> dateColumn;

    @FXML private Button viewButton;
    @FXML private Button updateStatusButton;
    @FXML private Button refreshButton;
    @FXML private ProgressIndicator progressIndicator;

    private ObservableList<Feedback> feedbackList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTable();
        refreshData();
    }

    private void setupTable() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        memberColumn.setCellValueFactory(new PropertyValueFactory<>("memberName"));
        subjectColumn.setCellValueFactory(new PropertyValueFactory<>("subject"));
        ratingColumn.setCellValueFactory(new PropertyValueFactory<>("rating"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // 3. CellFactory nhận LocalDateTime
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        dateColumn.setCellFactory(column -> new TableCell<Feedback, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                }
            }
        });

        feedbackTable.setItems(feedbackList);
        feedbackTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            boolean has = newSel != null;
            viewButton.setDisable(!has);
            updateStatusButton.setDisable(!has);
        });
    }

    @FXML
    public void refreshData() {
        setLoading(true);

        Task<Void> refreshTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                HttpResponse<String> response = ApiClient.getInstance().get(ApiConfig.FEEDBACK);

                if (response.statusCode() == 200) {
                    List<Feedback> feedback = ApiClient.getInstance().getObjectMapper()
                            .readValue(response.body(), new TypeReference<List<Feedback>>() {});

                    Platform.runLater(() -> {
                        feedbackList.clear();
                        feedbackList.addAll(feedback);
                    });
                } else {
                    Platform.runLater(() -> {
                        AlertHelper.showError("Error", "Failed to load feedback data.");
                    });
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
        };

        new Thread(refreshTask).start();
    }

    @FXML
    private void handleView() {
        Feedback selected = feedbackTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            showFeedbackDetails(selected);
        }
    }

    @FXML
    private void handleUpdateStatus() {
        Feedback selected = feedbackTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            updateFeedbackStatus(selected);
        }
    }

    private void showFeedbackDetails(Feedback feedback) {
        StringBuilder details = new StringBuilder();
        details.append("ID: ").append(feedback.getId()).append("\n");
        details.append("Member: ").append(feedback.getMemberName()).append("\n");
        details.append("Subject: ").append(feedback.getSubject()).append("\n");
        details.append("Rating: ").append(feedback.getRating()).append("/5\n");
        details.append("Status: ").append(feedback.getStatus()).append("\n");
        details.append("Date: ").append(feedback.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))).append("\n\n");
        details.append("Message:\n").append(feedback.getMessage());

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Feedback Details");
        alert.setHeaderText("Feedback #" + feedback.getId());
        alert.setContentText(details.toString());
        alert.getDialogPane().setPrefWidth(500);
        alert.showAndWait();
    }

    private void updateFeedbackStatus(Feedback feedback) {
        ChoiceDialog<String> dialog = new ChoiceDialog<>(feedback.getStatus(), "Pending", "Reviewed", "Resolved");
        dialog.setTitle("Update Status");
        dialog.setHeaderText("Update feedback status");
        dialog.setContentText("Choose new status:");

        dialog.showAndWait().ifPresent(newStatus -> {
            if (!newStatus.equals(feedback.getStatus())) {
                updateStatus(feedback, newStatus);
            }
        });
    }

    private void updateStatus(Feedback feedback, String newStatus) {
        setLoading(true);

        Task<Void> updateTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                feedback.setStatus(newStatus);
                HttpResponse<String> response = ApiClient.getInstance().put(
                    ApiConfig.FEEDBACK + "/" + feedback.getId(), feedback);

                if (response.statusCode() == 200) {
                    Platform.runLater(() -> {
                        AlertHelper.showInfo("Success", "Feedback status updated successfully.");
                        refreshData();
                    });
                } else {
                    Platform.runLater(() -> {
                        AlertHelper.showError("Error", "Failed to update feedback status.");
                    });
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
        };

        new Thread(updateTask).start();
    }

    private void setLoading(boolean loading) {
        Platform.runLater(() -> {
            progressIndicator.setVisible(loading);
            viewButton.setDisable(loading);
            updateStatusButton.setDisable(loading);
            refreshButton.setDisable(loading);
        });
    }

    @FXML private void handleAdd()    { openFeedbackForm(null); }
    @FXML private void handleEdit()   {
        Feedback sel = feedbackTable.getSelectionModel().getSelectedItem();
        if (sel != null) openFeedbackForm(sel);
    }
    @FXML private void handleDelete() {
        Feedback sel = feedbackTable.getSelectionModel().getSelectedItem();
        if (sel != null && AlertHelper.showConfirmation(
                "Confirm Delete",
                "Are you sure you want to delete this feedback?")) {
            deleteFeedback(sel);
        }
    }

    private void deleteFeedback(Feedback feedback) {
        setLoading(true);
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                HttpResponse<String> resp = ApiClient.getInstance()
                        .delete(ApiConfig.FEEDBACK + "/" + feedback.getId());

                if (resp.statusCode() == 200 || resp.statusCode() == 204) {
                    Platform.runLater(() -> {
                        feedbackList.remove(feedback);
                        AlertHelper.showInfo("Success", "Feedback deleted successfully.");
                    });
                } else {
                    Platform.runLater(() ->
                            AlertHelper.showError("Error", "Failed to delete feedback.")
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

    private void openFeedbackForm(Feedback feedback) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Feedback/FeedbackForm.fxml"));
            Scene scene = new Scene(loader.load(), 500, 400);
            FeedbackFormController ctl = loader.getController();
            ctl.setFeedback(feedback);
            ctl.setParentController(this);

            Stage stage = new Stage();
            stage.setTitle(feedback == null ? "Add Feedback" : "Edit Feedback");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.showAndWait();
        } catch (Exception e) {
            AlertHelper.showError("Error", "Could not load feedback form.");
        }
    }
}
