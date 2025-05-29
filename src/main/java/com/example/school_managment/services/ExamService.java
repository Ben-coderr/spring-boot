package com.example.school_managment.services;

import com.example.school_managment.models.Exam;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ExamService {
    private final ObservableList<Exam> exams = FXCollections.observableArrayList();

    public ObservableList<Exam> getAllExams() {
        return FXCollections.observableArrayList(exams);
    }

    public void addExam(Exam exam) {
        exams.add(exam);
    }

    public void deleteExam(Exam exam) {
        exams.remove(exam);
    }

    public void updateExam(Exam oldExam, Exam newExam) {
        int index = exams.indexOf(oldExam);
        if (index != -1) {
            exams.set(index, newExam);
        }
    }
}