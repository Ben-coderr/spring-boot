package com.example.school_managment.services;

import com.example.school_managment.models.Assignment;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class AssignmentService {
    private final ObservableList<Assignment> assignments = FXCollections.observableArrayList();

    public ObservableList<Assignment> getAllAssignments() {
        return FXCollections.observableArrayList(assignments);
    }

    public void addAssignment(Assignment assignment) {
        assignments.add(assignment);
    }

    public void deleteAssignment(Assignment assignment) {
        assignments.remove(assignment);
    }

    public void updateAssignment(Assignment oldAssignment, Assignment newAssignment) {
        int index = assignments.indexOf(oldAssignment);
        if (index != -1) {
            assignments.set(index, newAssignment);
        }
    }
}