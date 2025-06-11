package com.gymapp.controller.WorkoutSession;

import com.fasterxml.jackson.core.type.TypeReference;
import com.gymapp.api.ApiClient;
import com.gymapp.api.ApiConfig;
import com.gymapp.model.WorkoutSession;
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
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class WorkoutSessionController implements Initializable {
    
    @FXML private TableView<WorkoutSession> sessionTable;
    @FXML private TableColumn<WorkoutSession, Long> idColumn;
    @FXML private TableColumn<WorkoutSession, String> memberColumn;
    @FXML private TableColumn<WorkoutSession, String> roomColumn;
    @FXML private TableColumn<WorkoutSession, String> typeColumn;
    @FXML private TableColumn<WorkoutSession, String> startTimeColumn;
    @FXML private TableColumn<WorkoutSession, String> statusColumn;
    @FXML private TableColumn<WorkoutSession, String> trainerColumn;
    @FXML private Button addButton;
    @FXML private Button editButton;
    @FXML private Button deleteButton;
    @FXML private Button refreshButton;
    @FXML private ProgressIndicator progressIndicator;
    
    private ObservableList<WorkoutSession> sessionList = FXCollections.observableArrayList();
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTable();
        refreshData();
    }
    
    private void setupTable() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        memberColumn.setCellValueFactory(new PropertyValueFactory<>("memberName"));
        roomColumn.setCellValueFactory(new PropertyValueFactory<>("roomName"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("sessionType"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        trainerColumn.setCellValueFactory(new PropertyValueFactory<>("trainerName"));
        
        // Format start time column
        startTimeColumn.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        startTimeColumn.setCellFactory(column -> new TableCell<WorkoutSession, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    WorkoutSession session = getTableView().getItems().get(getIndex());
                    if (session.getStartTime() != null) {
                        setText(session.getStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                    }
                }
            }
        });
        
        sessionTable.setItems(sessionList);
        
        // Enable/disable buttons based on selection
        sessionTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean hasSelection = newSelection != null;
            editButton.setDisable(!hasSelection);
            deleteButton.setDisable(!hasSelection);
        });
    }
    
    @FXML
    private void refreshData() {
        setLoading(true);
        
        Task<Void> refreshTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                HttpResponse<String> response = ApiClient.getInstance().get(ApiConfig.WORKOUT_SESSIONS);
                
                if (response.statusCode() == 200) {
                    List<WorkoutSession> sessions = ApiClient.getInstance().getObjectMapper()
                            .readValue(response.body(), new TypeReference<List<WorkoutSession>>() {});
                    
                    Platform.runLater(() -> {
                        sessionList.clear();
                        sessionList.addAll(sessions);
                    });
                } else {
                    Platform.runLater(() -> {
                        AlertHelper.showError("Error", "Failed to load session data.");
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
    private void handleAdd() {
        openSessionForm(null);
    }
    
    @FXML
    private void handleEdit() {
        WorkoutSession selected = sessionTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            openSessionForm(selected);
        }
    }
    
    @FXML
    private void handleDelete() {
        WorkoutSession selected = sessionTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            if (AlertHelper.showConfirmation("Confirm Delete", "Are you sure you want to delete this session?")) {
                deleteSession(selected);
            }
        }
    }
    
    private void deleteSession(WorkoutSession session) {
        setLoading(true);
        
        Task<Void> deleteTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                HttpResponse<String> response = ApiClient.getInstance().delete(ApiConfig.WORKOUT_SESSIONS + "/" + session.getId());
                
                if (response.statusCode() == 200 || response.statusCode() == 204) {
                    Platform.runLater(() -> {
                        sessionList.remove(session);
                        AlertHelper.showInfo("Success", "Session deleted successfully.");
                    });
                } else {
                    Platform.runLater(() -> {
                        AlertHelper.showError("Error", "Failed to delete session.");
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
        
        new Thread(deleteTask).start();
    }
    
    private void openSessionForm(WorkoutSession session) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/WorkoutSession/WorkoutSessionForm.fxml"));
            Scene scene = new Scene(loader.load(), 500, 600);
            
            WorkoutSessionFormController controller = loader.getController();
            controller.setWorkoutSession(session);
            controller.setParentController(this);
            
            Stage stage = new Stage();
            stage.setTitle(session == null ? "Add Session" : "Edit Session");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            AlertHelper.showError("Error", "Could not load session form.");
        }
    }
    
    public void onSessionSaved() {
        refreshData();
    }
    
    private void setLoading(boolean loading) {
        Platform.runLater(() -> {
            progressIndicator.setVisible(loading);
            addButton.setDisable(loading);
            editButton.setDisable(loading);
            deleteButton.setDisable(loading);
            refreshButton.setDisable(loading);
        });
    }
}
