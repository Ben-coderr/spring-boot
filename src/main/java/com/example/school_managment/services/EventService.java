package com.example.school_managment.services;

import com.example.school_managment.models.Event;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class EventService {
    private final ObservableList<Event> events = FXCollections.observableArrayList();

    public ObservableList<Event> getAllEvents() {
        return FXCollections.observableArrayList(events);
    }

    public void addEvent(Event event) {
        events.add(event);
    }

    public void deleteEvent(Event event) {
        events.remove(event);
    }

    public void updateEvent(Event oldEvent, Event newEvent) {
        int index = events.indexOf(oldEvent);
        if (index != -1) {
            events.set(index, newEvent);
        }
    }
}