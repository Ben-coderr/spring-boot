package com.example.school_managment.services;

import com.example.school_managment.models.Result;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ResultService {
    private final ObservableList<Result> results = FXCollections.observableArrayList();

    public ObservableList<Result> getAllResults() {
        return FXCollections.observableArrayList(results);
    }

    public void addResult(Result result) {
        results.add(result);
    }

    public void deleteResult(Result result) {
        results.remove(result);
    }

    public void updateResult(Result oldResult, Result newResult) {
        int index = results.indexOf(oldResult);
        if (index != -1) {
            results.set(index, newResult);
        }
    }
}