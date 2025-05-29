package com.example.school_managment.services;

import com.example.school_managment.models.Subject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class SubjectService {
    private final ObservableList<Subject> subjects = FXCollections.observableArrayList();

    public ObservableList<Subject> getAllSubjects() {
        return FXCollections.observableArrayList(subjects);
    }

    public void addSubject(Subject subject) {
        subjects.add(subject);
    }

    public void deleteSubject(Subject subject) {
        subjects.remove(subject);
    }

    public void updateSubject(Subject oldSubject, Subject newSubject) {
        int index = subjects.indexOf(oldSubject);
        if (index != -1) {
            subjects.set(index, newSubject);
        }
    }
}