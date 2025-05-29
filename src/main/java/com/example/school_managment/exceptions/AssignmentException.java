package com.example.school_managment.exceptions;

public class AssignmentException extends Exception {
    public AssignmentException(String message) {
        super(message);
    }

    public AssignmentException(String message, Throwable cause) {
        super(message, cause);
    }
}
