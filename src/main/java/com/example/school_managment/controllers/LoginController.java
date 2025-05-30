package com.example.school_managment.controllers;

import com.example.school_managment.api.ApiClient;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    public void initialize() {
        // Remove the old AuthService usage:
        // authService = new AuthService();

        emailField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                passwordField.requestFocus();
            }
        });

        passwordField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                handleLogin();
            }
        });
    }

    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            showError("Please enter both email and password");
            return;
        }

        // Use ApiClient to do the actual login request
        try {
            ApiClient.login(email, password);

            // If login succeeds, load the main view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/main-view.fxml"));
            Parent root = loader.load();

            MainController mainController = loader.getController();
            // Optionally set user info: mainController.setCurrentUserEmail(email);

            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(new Scene(root, 900, 600));
            stage.setTitle("EduSphere - School Management System");
            stage.setMaximized(true);
            stage.centerOnScreen();

        } catch (Exception ex) {
            ex.printStackTrace();
            showError("Login failed: " + ex.getMessage());
        }
    }

    @FXML
    private void handleSignUp() {
        // Not implemented with REST
        showError("Sign up is not implemented yet.");
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}
