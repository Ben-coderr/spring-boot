package com.example.school_managment.models;

import javafx.beans.property.SimpleStringProperty;

public class Subject {
    private final SimpleStringProperty name;
    private final SimpleStringProperty teachers;

    public Subject(String name, String teachers) {
        this.name = new SimpleStringProperty(name);
        this.teachers = new SimpleStringProperty(teachers);
    }

    // Getters and property methods
    public String getName() {
        return name.get();
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public String getTeachers() {
        return teachers.get();
    }

    public SimpleStringProperty teachersProperty() {
        return teachers;
    }
}