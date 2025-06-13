package com.gymapp.controller.Member;

import com.fasterxml.jackson.core.type.TypeReference;
import com.gymapp.api.ApiClient;
import com.gymapp.api.ApiConfig;
import com.gymapp.model.Member;
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
import java.util.List;
import java.util.ResourceBundle;

public class MemberController implements Initializable {

    @FXML private TableView<Member> memberTable;
    @FXML private TableColumn<Member, Long> idColumn;
    @FXML private TableColumn<Member, String> nameColumn;
    @FXML private TableColumn<Member, String> emailColumn;
    @FXML private TableColumn<Member, String> phoneColumn;
    @FXML private TableColumn<Member, String> membershipColumn;
    @FXML private TableColumn<Member, String> statusColumn;
    @FXML private Button addButton;
    @FXML private Button editButton;
    @FXML private Button deleteButton;
    @FXML private Button refreshButton;
    @FXML private ProgressIndicator progressIndicator;

    private final ObservableList<Member> memberList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTable();
        refreshData();
    }

    private void setupTable() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        membershipColumn.setCellValueFactory(new PropertyValueFactory<>("membershipType"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        memberTable.setItems(memberList);
        memberTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            boolean sel = newSel != null;
            editButton.setDisable(!sel);
            deleteButton.setDisable(!sel);
        });
    }

    @FXML
    private void refreshData() {
        setLoading(true);
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                // Đổi MEMBER -> MEMBERS
                HttpResponse<String> response = ApiClient.getInstance()
                        .get(ApiConfig.MEMBERS);

                if (response.statusCode() == 200) {
                    List<Member> list = ApiClient.getInstance()
                            .getObjectMapper()
                            .readValue(response.body(), new TypeReference<List<Member>>() {});

                    Platform.runLater(() -> {
                        memberList.setAll(list);
                    });
                } else {
                    Platform.runLater(() ->
                            AlertHelper.showError("Error", "Failed to load member data.")
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
    @FXML private void handleEdit()   {
        Member sel = memberTable.getSelectionModel().getSelectedItem();
        if (sel != null) openMemberForm(sel);
    }
    @FXML private void handleDelete() {
        Member sel = memberTable.getSelectionModel().getSelectedItem();
        if (sel != null && AlertHelper.showConfirmation(
                "Confirm Delete",
                "Are you sure you want to delete this member?")) {
            deleteMember(sel);
        }
    }

    private void deleteMember(Member member) {
        setLoading(true);
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                // Đổi MEMBER -> MEMBERS
                HttpResponse<String> resp = ApiClient.getInstance()
                        .delete(ApiConfig.MEMBERS + "/" + member.getId());

                if (resp.statusCode() == 200 || resp.statusCode() == 204) {
                    Platform.runLater(() -> {
                        memberList.remove(member);
                        AlertHelper.showInfo("Success", "Member deleted successfully.");
                    });
                } else {
                    Platform.runLater(() ->
                            AlertHelper.showError("Error", "Failed to delete member.")
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

    private void openMemberForm(Member member) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Member/MemberForm.fxml"));
            Scene scene = new Scene(loader.load(), 500, 600);
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());

            MemberFormController ctl = loader.getController();
            ctl.setMember(member);
            ctl.setParentController(this);

            Stage stage = new Stage();
            stage.setTitle(member == null ? "Add Member" : "Edit Member");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.showAndWait();
        } catch (Exception e) {
            AlertHelper.showError("Error", "Could not load member form.");
        }
    }

    public void onMemberSaved() {
        refreshData();
    }

    private void setLoading(boolean loading) {
        Platform.runLater(() -> {
            progressIndicator.setVisible(loading);
            editButton.setDisable(loading);
            deleteButton.setDisable(loading);
            refreshButton.setDisable(loading);
        });
    }
}
