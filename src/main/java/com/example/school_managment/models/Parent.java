package com.example.school_managment.models;

import javafx.beans.property.SimpleStringProperty;

public class Parent {
    private final SimpleStringProperty name;
    private final SimpleStringProperty info;
    private final SimpleStringProperty studentNames;
    private final SimpleStringProperty phone;
    private final SimpleStringProperty address;

    public Parent(String name, String info, String studentNames, String phone, String address) {
        this.name = new SimpleStringProperty(name);
        this.info = new SimpleStringProperty(info);
        this.studentNames = new SimpleStringProperty(studentNames);
        this.phone = new SimpleStringProperty(phone);
        this.address = new SimpleStringProperty(address);
    }

    // Getters
    public String getName() { return name.get(); }
    public String getInfo() { return info.get(); }
    public String getStudentNames() { return studentNames.get(); }
    public String getPhone() { return phone.get(); }
    public String getAddress() { return address.get(); }
}