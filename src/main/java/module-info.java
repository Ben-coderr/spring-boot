module com.example.school_managment {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;


    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.annotation;


    opens com.example.school_managment to javafx.fxml;
    opens com.example.school_managment.controllers to javafx.fxml;
    opens com.example.school_managment.models to javafx.base;

    exports com.example.school_managment;
}
