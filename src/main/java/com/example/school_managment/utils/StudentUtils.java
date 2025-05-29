package com.example.school_managment.utils;

import com.example.school_managment.models.Student;
import javafx.scene.control.Alert;

public class StudentUtils {

    public static boolean validateStudent(Student student) {
        if (student.getName() == null || student.getName().trim().isEmpty()) {
            showAlert("Invalid Name", "Student name cannot be empty.");
            return false;
        }

        if (student.getId() == null || student.getId().trim().isEmpty()) {
            showAlert("Invalid ID", "Student ID cannot be empty.");
            return false;
        }

        if (student.getPhone() == null || student.getPhone().trim().isEmpty()) {
            showAlert("Invalid Phone", "Student phone number cannot be empty.");
            return false;
        }

        if (student.getAddress() == null || student.getAddress().trim().isEmpty()) {
            showAlert("Invalid Address", "Student address cannot be empty.");
            return false;
        }

        return true;
    }

    public static void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static String formatPhoneNumber(String phone) {
        if (phone == null || phone.length() != 10) {
            return phone;
        }

        return phone.substring(0, 3) + "-" + phone.substring(3, 6) + "-" + phone.substring(6);
    }
}