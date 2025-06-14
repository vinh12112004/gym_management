package com.gymapp.controller.WorkoutSession;

import com.gymapp.api.ApiClient;
import com.gymapp.api.ApiConfig;
import com.gymapp.model.WorkoutSession;
import com.gymapp.util.AlertHelper;
import com.gymapp.util.ValidationUtil;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class WorkoutSessionFormController implements Initializable {
    
    @FXML private Label titleLabel;
    @FXML private TextField memberIdField;
    @FXML private TextField roomIdField;
    @FXML private ComboBox<String> sessionTypeComboBox;
    @FXML private DatePicker startDatePicker;
    @FXML private TextField startTimeField;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private TextField trainerField;
    @FXML private TextArea notesArea;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;
    @FXML private ProgressIndicator progressIndicator;
    
    private WorkoutSession workoutSession;
    private WorkoutSessionController parentController;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        sessionTypeComboBox.setItems(FXCollections.observableArrayList(
            "Personal Training", "Group Fitness", "Cardio", "Weight Training", "Yoga", "Pilates", "Boxing", "Swimming"));
        sessionTypeComboBox.setValue("Personal Training");
        
        statusComboBox.setItems(FXCollections.observableArrayList(
            "Scheduled", "In Progress", "Completed", "Cancelled", "No Show"));
        statusComboBox.setValue("Scheduled");
        
        startDatePicker.setValue(LocalDate.now());
        startTimeField.setText("09:00");
    }
    
    public void setWorkoutSession(WorkoutSession workoutSession) {
        this.workoutSession = workoutSession;
        if (workoutSession != null) {
            titleLabel.setText("Edit Workout Session");
            if (workoutSession.getMemberId() != null) {
                memberIdField.setText(workoutSession.getMemberId().toString());
            }
            if (workoutSession.getRoomId() != null) {
                roomIdField.setText(workoutSession.getRoomId().toString());
            }
            sessionTypeComboBox.setValue(workoutSession.getSessionType());
            if (workoutSession.getStartTime() != null) {
                startDatePicker.setValue(workoutSession.getStartTime().toLocalDate());
                startTimeField.setText(workoutSession.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")));
            }
            statusComboBox.setValue(workoutSession.getStatus());
            trainerField.setText(workoutSession.getTrainerName());
            notesArea.setText(workoutSession.getNotes());
        }
    }
    
    public void setParentController(WorkoutSessionController parentController) {
        this.parentController = parentController;
    }
    
    @FXML
    private void handleSave() {
        if (!validateInput()) {
            return;
        }
        
        setLoading(true);
        
        Task<Void> saveTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                WorkoutSession sessionToSave = createSessionFromForm();
                
                HttpResponse<String> response;
                if (workoutSession == null) {
                    // Create new session
                    response = ApiClient.getInstance().post(ApiConfig.WORKOUT_SESSIONS, sessionToSave);
                } else {
                    // Update existing session
                    sessionToSave.setId(workoutSession.getId());
                    response = ApiClient.getInstance().put(ApiConfig.WORKOUT_SESSIONS + "/" + workoutSession.getId(), sessionToSave);
                }
                
                if (response.statusCode() == 200 || response.statusCode() == 201) {
                    Platform.runLater(() -> {
                        AlertHelper.showInfo("Success", "Session saved successfully.");
                        if (parentController != null) {
                            parentController.onSessionSaved();
                        }
                        closeWindow();
                    });
                } else {
                    Platform.runLater(() -> {
                        AlertHelper.showError("Error", "Failed to save session.");
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
        
        new Thread(saveTask).start();
    }
    
    @FXML
    private void handleCancel() {
        closeWindow();
    }
    
    private boolean validateInput() {
        if (!ValidationUtil.isValidInteger(memberIdField.getText())) {
            AlertHelper.showWarning("Validation Error", "Please enter a valid member ID.");
            return false;
        }
        
        if (!ValidationUtil.isValidInteger(roomIdField.getText())) {
            AlertHelper.showWarning("Validation Error", "Please enter a valid room ID.");
            return false;
        }
        
        if (startDatePicker.getValue() == null) {
            AlertHelper.showWarning("Validation Error", "Start date is required.");
            return false;
        }
        
        if (!ValidationUtil.isNotEmpty(startTimeField.getText())) {
            AlertHelper.showWarning("Validation Error", "Start time is required.");
            return false;
        }
        
        // Validate time format
        try {
            LocalTime.parse(startTimeField.getText(), DateTimeFormatter.ofPattern("HH:mm"));
        } catch (Exception e) {
            AlertHelper.showWarning("Validation Error", "Please enter time in HH:MM format.");
            return false;
        }
        
        return true;
    }
    
    private WorkoutSession createSessionFromForm() {
        WorkoutSession session = new WorkoutSession();
        session.setMemberId(Long.parseLong(memberIdField.getText().trim()));
        session.setRoomId(Long.parseLong(roomIdField.getText().trim()));
        session.setSessionType(sessionTypeComboBox.getValue());
        session.setStatus(statusComboBox.getValue());
        session.setTrainerName(trainerField.getText().trim());
        session.setNotes(notesArea.getText().trim());
        
        // Combine date and time
        LocalDate date = startDatePicker.getValue();
        LocalTime time = LocalTime.parse(startTimeField.getText(), DateTimeFormatter.ofPattern("HH:mm"));
        session.setStartTime(LocalDateTime.of(date, time));
        
        return session;
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
