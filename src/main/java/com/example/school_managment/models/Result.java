package com.example.school_managment.models;

import javafx.beans.property.*;

import java.time.LocalDate;

public class Result {
    private final StringProperty subjectName;
    private final StringProperty student;
    private final IntegerProperty score;
    private final StringProperty teacher;
    private final StringProperty className;
    private final ObjectProperty<LocalDate> date;

    public Result(String subjectName, String student, int score, String teacher, String className, LocalDate date) {
        this.subjectName = new SimpleStringProperty(subjectName);
        this.student = new SimpleStringProperty(student);
        this.score = new SimpleIntegerProperty(score);
        this.teacher = new SimpleStringProperty(teacher);
        this.className = new SimpleStringProperty(className);
        this.date = new SimpleObjectProperty<>(date);
    }

    // Getters
    public String getSubjectName() { return subjectName.get(); }
    public String getStudent() { return student.get(); }
    public int getScore() { return score.get(); }
    public String getTeacher() { return teacher.get(); }
    public String getClassName() { return className.get(); }
    public LocalDate getDate() { return date.get(); }

    // Property getters (for JavaFX bindings)
    public StringProperty subjectNameProperty() { return subjectName; }
    public StringProperty studentProperty() { return student; }
    public IntegerProperty scoreProperty() { return score; }
    public StringProperty teacherProperty() { return teacher; }
    public StringProperty classNameProperty() { return className; }
    public ObjectProperty<LocalDate> dateProperty() { return date; }
}