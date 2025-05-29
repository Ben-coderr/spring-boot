package com.example.school_managment.services;

import com.example.school_managment.models.Parent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ParentService {
    private final ObservableList<Parent> parents = FXCollections.observableArrayList();

    public ObservableList<Parent> getAllParents() {
        return FXCollections.observableArrayList(parents);
    }

    public void addParent(Parent parent) {
        parents.add(parent);
    }

    public void deleteParent(Parent parent) {
        parents.remove(parent);
    }

    public void updateParent(Parent oldParent, Parent newParent) {
        int index = parents.indexOf(oldParent);
        if (index != -1) {
            parents.set(index, newParent);
        }
    }
}