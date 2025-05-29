package com.example.school_managment.models;

import javafx.beans.property.SimpleStringProperty;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class Assignment {
    private final SimpleStringProperty name;
    private final SimpleStringProperty className;
    private final SimpleStringProperty teacherName;
    private final SimpleStringProperty date;

    public Assignment(String name, String className, String teacherName, String date) {
        validateDate(date);
        this.name = new SimpleStringProperty(name);
        this.className = new SimpleStringProperty(className);
        this.teacherName = new SimpleStringProperty(teacherName);
        this.date = new SimpleStringProperty(date);
    }

    private void validateDate(String date) {
        try {
            LocalDate.parse(date);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format. Use YYYY-MM-DD");
        }
    }

    // Getters and property methods
    public String getName() { return name.get(); }
    public SimpleStringProperty nameProperty() { return name; }

    public String getClassName() { return className.get(); }
    public SimpleStringProperty classNameProperty() { return className; }

    public String getTeacherName() { return teacherName.get(); }
    public SimpleStringProperty teacherNameProperty() { return teacherName; }

    public String getDate() { return date.get(); }
    public SimpleStringProperty dateProperty() { return date; }
}