package com.example.school_managment.services;

import com.example.school_managment.models.Teacher;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class TeacherService {
    private final ObservableList<Teacher> teachers = FXCollections.observableArrayList();

    public ObservableList<Teacher> getAllTeachers() {
        return FXCollections.observableArrayList(teachers);
    }

    public void addTeacher(Teacher teacher) {
        teachers.add(teacher);
    }

    public void deleteTeacher(Teacher teacher) {
        teachers.remove(teacher);
    }

    public void updateTeacher(Teacher oldTeacher, Teacher newTeacher) {
        int index = teachers.indexOf(oldTeacher);
        if (index != -1) {
            teachers.set(index, newTeacher);
        }
    }
}