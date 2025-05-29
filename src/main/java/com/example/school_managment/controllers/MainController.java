package com.example.school_managment.controllers;

import com.example.school_managment.models.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class MainController {
    @FXML private BorderPane rootPane;
    @FXML private BorderPane centerPane;

    private User currentUser;

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    // Navigation methods
    @FXML
    private void loadHome() {
        loadContent("hello-view.fxml");
    }

    @FXML
    private void loadTeachers() {
        loadContent("teacher-view.fxml");
    }

    @FXML
    private void loadStudents() {
        loadContent("student-view.fxml");
    }

    @FXML
    private void loadParents() {
        loadContent("parent-view.fxml");
    }

    @FXML
    private void loadSubjects() {
        loadContent("subject-view.fxml");
    }

    @FXML
    private void loadClasses() {
        loadContent("class-view.fxml");
    }

    @FXML
    private void loadLessons() {
        loadContent("lesson-view.fxml");
    }

    @FXML
    private void loadExams() {
        loadContent("exam-view.fxml");
    }

    @FXML
    private void loadAssignments() {
        loadContent("assignment-view.fxml");
    }

    @FXML
    private void loadResults() {
        loadContent("result-view.fxml");
    }

    @FXML
    private void loadAttendance() {
        showAlert("Navigation", "Attendance page loaded");
    }

    @FXML
    private void loadEvents() {
        loadContent("event-view.fxml");
    }

    @FXML
    private void loadMessages() {
        showAlert("Navigation", "Messages page loaded");
    }

    @FXML
    private void loadAnnouncements() {
        showAlert("Navigation", "Announcements page loaded");
    }

    @FXML
    private void loadProfile() {
        if (currentUser != null) {
            showAlert("Profile", "Logged in as: " + currentUser.getEmail());
        } else {
            showAlert("Profile", "No user information available");
        }
    }

    @FXML
    private void loadSettings() {
        showAlert("Navigation", "Settings page loaded");
    }

    @FXML
    private void logout() {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Logout");
        confirmAlert.setHeaderText("Logout");
        confirmAlert.setContentText("Are you sure you want to logout?");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // Load login view
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/login-view.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) rootPane.getScene().getWindow();
                stage.setScene(new Scene(root, 400, 600));
                stage.setTitle("EduSphere - Login");
                stage.setMaximized(false);
                stage.centerOnScreen();
            } catch (IOException e) {
                showAlert("Error", "Failed to logout: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void loadContent(String fxml) {
        try {
            Node view = FXMLLoader.load(getClass().getResource("/views/" + fxml));
            centerPane.setCenter(view); // Only replaces the center slot
        } catch (IOException ex) {
            showAlert("Error", "Failed to load content: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}