package com.example.school_managment.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import com.example.school_managment.models.User;
import com.example.school_managment.services.AuthService;

import java.io.IOException;

public class LoginController {
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    private AuthService authService;

    public void initialize() {
        authService = new AuthService();

        // Add enter key handling for field navigation
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

        // Validate input
        if (email.isEmpty() || password.isEmpty()) {
            showError("Please enter both email and password");
            return;
        }

        // Validate email format
        if (!isValidEmail(email)) {
            showError("Please enter a valid email address");
            return;
        }

        // Authenticate user
        User user = authService.authenticate(email, password);
        if (user != null) {
            try {
                // Load main application view
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/main-view.fxml"));
                Parent root = loader.load();

                // Pass user to MainController
                MainController mainController = loader.getController();
                mainController.setCurrentUser(user);

                // Get current stage
                Stage stage = (Stage) emailField.getScene().getWindow();

                // Set new scene
                stage.setScene(new Scene(root));
                stage.setTitle("School Management System");
                stage.setMaximized(true);
                stage.centerOnScreen();
            } catch (IOException e) {
                showError("Failed to load application");
                e.printStackTrace();
            }
        } else {
            showError("Invalid email or password");
        }
    }

    @FXML
    private void handleSignUp() {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        // Validate input
        if (email.isEmpty() || password.isEmpty()) {
            showError("Please enter both email and password");
            return;
        }

        // Validate email format
        if (!isValidEmail(email)) {
            showError("Please enter a valid email address");
            return;
        }

        // Check if user already exists
        if (authService.userExists(email)) {
            showError("Email already registered");
            return;
        }

        // Register new user
        authService.registerUser(new User(email, password));
        showError("Registration successful! Please login.");

        // Clear fields
        emailField.clear();
        passwordField.clear();
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email.matches(emailRegex);
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}