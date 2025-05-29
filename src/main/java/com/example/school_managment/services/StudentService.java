package com.example.school_managment.services;

import com.example.school_managment.models.Student;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class StudentService {
    private final ObservableList<Student> students = FXCollections.observableArrayList();

    public ObservableList<Student> getAllStudents() {
        return FXCollections.observableArrayList(students);
    }

    public void addStudent(Student student) {
        students.add(student);
    }

    public void deleteStudent(Student student) {
        students.remove(student);
    }

    public void updateStudent(Student oldStudent, Student newStudent) {
        int index = students.indexOf(oldStudent);
        if (index != -1) {
            students.set(index, newStudent);
        }
    }
}