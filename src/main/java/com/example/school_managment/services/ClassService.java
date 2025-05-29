package com.example.school_managment.services;

import com.example.school_managment.models.Class;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ClassService {
    private final ObservableList<Class> classes = FXCollections.observableArrayList();

    public ObservableList<Class> getAllClasses() {
        return FXCollections.observableArrayList(classes);
    }

    public void addClass(Class classObj) {
        classes.add(classObj);
    }

    public void deleteClass(Class classObj) {
        classes.remove(classObj);
    }

    public void updateClass(Class oldClass, Class newClass) {
        int index = classes.indexOf(oldClass);
        if (index != -1) {
            classes.set(index, newClass);
        }
    }
}