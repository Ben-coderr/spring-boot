package com.example.school_managment.models;

import javafx.beans.property.SimpleStringProperty;

public class Lesson {
    private final SimpleStringProperty subjectName;
    private final SimpleStringProperty className;
    private final SimpleStringProperty teacher;

    public Lesson(String subjectName, String className, String teacher) {
        this.subjectName = new SimpleStringProperty(subjectName);
        this.className = new SimpleStringProperty(className);
        this.teacher = new SimpleStringProperty(teacher);
    }

    // Getters and property methods
    public String getSubjectName() {
        return subjectName.get();
    }

    public SimpleStringProperty subjectNameProperty() {
        return subjectName;
    }

    public String getClassName() {
        return className.get();
    }

    public SimpleStringProperty classNameProperty() {
        return className;
    }

    public String getTeacher() {
        return teacher.get();
    }

    public SimpleStringProperty teacherProperty() {
        return teacher;
    }
}