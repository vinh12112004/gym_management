package com.gymapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.gymapp.util.SessionManager;

import java.net.URL;

public class GymManagementApp extends Application {
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Initialize session manager
        SessionManager.getInstance().setPrimaryStage(primaryStage);
        
        // Load login screen first
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Auth/Login.fxml"));
        Scene scene = new Scene(loader.load(), 800, 400);
        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
        primaryStage.setTitle("Gym Management System");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
