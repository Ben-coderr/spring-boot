package com.example.school_managment.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        // Simple validation
        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter both username and password");
            return;
        }

        // Simple authentication (replace with your logic)
        if (authenticate(username, password)) {
            try {
                // Load main application view
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/main-view.fxml"));
                Parent root = loader.load();

                // Get current stage
                Stage stage = (Stage) usernameField.getScene().getWindow();

                // Set new scene
                stage.setScene(new Scene(root));
                stage.setTitle("School Management System");
                stage.centerOnScreen();
            } catch (IOException e) {
                showError("Failed to load application");
                e.printStackTrace();
            }
        } else {
            showError("Invalid username or password");
        }
    }

    private boolean authenticate(String username, String password) {
        // Replace with your actual authentication logic
        return username.equals("admin") && password.equals("admin123");
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}