package com.example.school_managment.services;

import com.example.school_managment.models.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class AuthService {
    private final ObservableList<User> users = FXCollections.observableArrayList();

    public AuthService() {
        // Initialize with sample users
        users.add(new User("admin", "admin123"));
        users.add(new User("teacher", "teacher123"));
        users.add(new User("student", "student123"));
        users.add(new User("parent", "parent123"));
    }

    public User authenticate(String username, String password) {
        return users.stream()
                .filter(user -> user.getUsername().equals(username) && user.getPassword().equals(password))
                .findFirst()
                .orElse(null);
    }
}