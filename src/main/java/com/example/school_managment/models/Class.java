package com.example.school_managment.models;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Class {
    private final SimpleStringProperty name;
    private final SimpleIntegerProperty capacity;
    private final SimpleIntegerProperty grade;
    private final SimpleStringProperty supervisor;

    public Class(String name, int capacity, int grade, String supervisor) {
        this.name = new SimpleStringProperty(name);
        this.capacity = new SimpleIntegerProperty(capacity);
        this.grade = new SimpleIntegerProperty(grade);
        this.supervisor = new SimpleStringProperty(supervisor);
    }

    // Getters and property methods
    public String getName() {
        return name.get();
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public int getCapacity() {
        return capacity.get();
    }

    public SimpleIntegerProperty capacityProperty() {
        return capacity;
    }

    public int getGrade() {
        return grade.get();
    }

    public SimpleIntegerProperty gradeProperty() {
        return grade;
    }

    public String getSupervisor() {
        return supervisor.get();
    }

    public SimpleStringProperty supervisorProperty() {
        return supervisor;
    }
}