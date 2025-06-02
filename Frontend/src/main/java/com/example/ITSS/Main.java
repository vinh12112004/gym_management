package com.example.ITSS;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Register.fxml")); // Đổi thành tên file FXML của bạn
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.setTitle("Đăng ký");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}