package com.example.school_managment.exceptions;

public class ParentException extends Exception {
    public ParentException(String message) {
        super(message);
    }

    public ParentException(String message, Throwable cause) {
        super(message, cause);
    }
}
