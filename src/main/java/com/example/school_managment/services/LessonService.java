package com.example.school_managment.services;

import com.example.school_managment.models.Lesson;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class LessonService {
    private final ObservableList<Lesson> lessons = FXCollections.observableArrayList();

    public ObservableList<Lesson> getAllLessons() {
        return FXCollections.observableArrayList(lessons);
    }

    public void addLesson(Lesson lesson) {
        lessons.add(lesson);
    }

    public void deleteLesson(Lesson lesson) {
        lessons.remove(lesson);
    }

    public void updateLesson(Lesson oldLesson, Lesson newLesson) {
        int index = lessons.indexOf(oldLesson);
        if (index != -1) {
            lessons.set(index, newLesson);
        }
    }
}