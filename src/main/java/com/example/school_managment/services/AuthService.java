package com.example.school_managment.services;

import com.example.school_managment.models.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class AuthService {
    private final ObservableList<User> users = FXCollections.observableArrayList();

    public AuthService() {
        // Initialize with sample users
        users.add(new User("admin@school.com", "admin123"));
        users.add(new User("teacher@school.com", "teacher123"));
        users.add(new User("student@school.com", "student123"));
        users.add(new User("parent@school.com", "parent123"));
    }

    public User authenticate(String email, String password) {
        return users.stream()
                .filter(user -> user.getEmail().equals(email) && user.getPassword().equals(password))
                .findFirst()
                .orElse(null);
    }

    public boolean userExists(String email) {
        return users.stream().anyMatch(user -> user.getEmail().equals(email));
    }

    public void registerUser(User user) {
        users.add(user);
    }
}