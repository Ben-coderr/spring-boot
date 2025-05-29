package com.example.school_managment.models;

import javafx.beans.property.SimpleStringProperty;

public class Teacher {
    private final SimpleStringProperty name;
    private final SimpleStringProperty id;
    private final SimpleStringProperty email;
    private final SimpleStringProperty subjects;
    private final SimpleStringProperty classes;
    private final SimpleStringProperty phone;
    private final SimpleStringProperty address;

    public Teacher(String name, String id, String email, String subjects, String classes, String phone, String address) {
        this.name = new SimpleStringProperty(name);
        this.id = new SimpleStringProperty(id);
        this.email = new SimpleStringProperty(email);
        this.subjects = new SimpleStringProperty(subjects);
        this.classes = new SimpleStringProperty(classes);
        this.phone = new SimpleStringProperty(phone);
        this.address = new SimpleStringProperty(address);
    }

    // Getters and property methods
    public String getName() {
        return name.get();
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public String getId() {
        return id.get();
    }

    public SimpleStringProperty idProperty() {
        return id;
    }

    public String getEmail() {
        return email.get();
    }

    public SimpleStringProperty emailProperty() {
        return email;
    }

    public String getSubjects() {
        return subjects.get();
    }

    public SimpleStringProperty subjectsProperty() {
        return subjects;
    }

    public String getClasses() {
        return classes.get();
    }

    public SimpleStringProperty classesProperty() {
        return classes;
    }

    public String getPhone() {
        return phone.get();
    }

    public SimpleStringProperty phoneProperty() {
        return phone;
    }

    public String getAddress() {
        return address.get();
    }

    public SimpleStringProperty addressProperty() {
        return address;
    }
}