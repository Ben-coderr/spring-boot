package com.example.school_managment.models;

import javafx.beans.property.SimpleStringProperty;

public class Exam {
    private final SimpleStringProperty subjectName;
    private final SimpleStringProperty className;
    private final SimpleStringProperty teacher;
    private final SimpleStringProperty date;

    public Exam(String subjectName, String className, String teacher, String date) {
        this.subjectName = new SimpleStringProperty(subjectName);
        this.className = new SimpleStringProperty(className);
        this.teacher = new SimpleStringProperty(teacher);
        this.date = new SimpleStringProperty(date);
    }

    // Getters
    public String getSubjectName() { return subjectName.get(); }
    public String getClassName() { return className.get(); }
    public String getTeacher() { return teacher.get(); }
    public String getDate() { return date.get(); }
}