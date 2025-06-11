package com.gymapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.gymapp.util.SessionManager;

public class GymManagementApp extends Application {
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Initialize session manager
        SessionManager.getInstance().setPrimaryStage(primaryStage);
        
        // Load login screen first
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
        Scene scene = new Scene(loader.load(), 400, 300);
        
        primaryStage.setTitle("Gym Management System");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
