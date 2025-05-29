package com.example.school_managment.models;

import javafx.beans.property.*;
import java.time.LocalDate;
import java.time.LocalTime;

public class Event {
    private final StringProperty title;
    private final StringProperty className;
    private final ObjectProperty<LocalDate> date;
    private final ObjectProperty<LocalTime> startTime;
    private final ObjectProperty<LocalTime> endTime;

    public Event(String title, String className, LocalDate date, LocalTime startTime, LocalTime endTime) {
        this.title = new SimpleStringProperty(title);
        this.className = new SimpleStringProperty(className);
        this.date = new SimpleObjectProperty<>(date);
        this.startTime = new SimpleObjectProperty<>(startTime);
        this.endTime = new SimpleObjectProperty<>(endTime);
    }

    // Getters
    public String getTitle() { return title.get(); }
    public String getClassName() { return className.get(); }
    public LocalDate getDate() { return date.get(); }
    public LocalTime getStartTime() { return startTime.get(); }
    public LocalTime getEndTime() { return endTime.get(); }

    // Property getters (for JavaFX bindings)
    public StringProperty titleProperty() { return title; }
    public StringProperty classNameProperty() { return className; }
    public ObjectProperty<LocalDate> dateProperty() { return date; }
    public ObjectProperty<LocalTime> startTimeProperty() { return startTime; }
    public ObjectProperty<LocalTime> endTimeProperty() { return endTime; }
}