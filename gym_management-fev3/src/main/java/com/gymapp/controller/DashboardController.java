package com.gymapp.controller;

import com.gymapp.api.ApiClient;
import com.gymapp.api.ApiConfig;
import com.gymapp.model.Equipment;
import com.gymapp.model.Member;
import com.gymapp.model.Package;
import com.gymapp.model.Room;
import com.gymapp.model.WorkoutSession;
import com.fasterxml.jackson.core.type.TypeReference;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.concurrent.Task;

import java.net.http.HttpResponse;
import java.util.List;
import java.net.URL;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    @FXML private Label equipmentCountLabel;
    @FXML private Label membersCountLabel;
    @FXML private Label packagesCountLabel;
    @FXML private Label roomsCountLabel;
    @FXML private Label sessionsCountLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        refreshData();
    }

    private void refreshData() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                // 1. Equipment
                HttpResponse<String> eqResp = ApiClient.getInstance()
                        .get(ApiConfig.EQUIPMENTS);
                if (eqResp.statusCode() == 200) {
                    List<Equipment> list = ApiClient.getInstance()
                            .getObjectMapper()
                            .readValue(eqResp.body(), new TypeReference<List<Equipment>>() {});
                    Platform.runLater(() ->
                            equipmentCountLabel.setText(String.valueOf(list.size()))
                    );
                }

                // 2. Members
                HttpResponse<String> memResp = ApiClient.getInstance()
                        .get(ApiConfig.MEMBERS);
                if (memResp.statusCode() == 200) {
                    List<Member> list = ApiClient.getInstance()
                            .getObjectMapper()
                            .readValue(memResp.body(), new TypeReference<List<Member>>() {});
                    Platform.runLater(() ->
                            membersCountLabel.setText(String.valueOf(list.size()))
                    );
                }

                // 3. Packages
                HttpResponse<String> pkgResp = ApiClient.getInstance()
                        .get(ApiConfig.PACKAGES);
                if (pkgResp.statusCode() == 200) {
                    List<Package> list = ApiClient.getInstance()
                            .getObjectMapper()
                            .readValue(pkgResp.body(), new TypeReference<List<Package>>() {});
                    Platform.runLater(() ->
                            packagesCountLabel.setText(String.valueOf(list.size()))
                    );
                }

                // 4. Rooms
                HttpResponse<String> roomResp = ApiClient.getInstance()
                        .get(ApiConfig.ROOMS);
                if (roomResp.statusCode() == 200) {
                    List<Room> list = ApiClient.getInstance()
                            .getObjectMapper()
                            .readValue(roomResp.body(), new TypeReference<List<Room>>() {});
                    Platform.runLater(() ->
                            roomsCountLabel.setText(String.valueOf(list.size()))
                    );
                }

                // 5. Workout Sessions
                HttpResponse<String> sesResp = ApiClient.getInstance()
                        .get(ApiConfig.WORKOUT_SESSIONS);
                if (sesResp.statusCode() == 200) {
                    List<WorkoutSession> list = ApiClient.getInstance()
                            .getObjectMapper()
                            .readValue(sesResp.body(), new TypeReference<List<WorkoutSession>>() {});
                    Platform.runLater(() ->
                            sessionsCountLabel.setText(String.valueOf(list.size()))
                    );
                }

                return null;
            }
        };

        new Thread(task).start();
    }
}
