package com.example.school_managment.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class HelloController implements Initializable {
    @FXML
    private Label welcomeText;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        welcomeText.setText("Welcome to EduSphere School Management System!");
    }

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to EduSphere!");
    }
}