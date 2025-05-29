package com.example.school_managment.exceptions;

public class ClassException extends Exception {
    public ClassException(String message) {
        super(message);
    }

    public ClassException(String message, Throwable cause) {
        super(message, cause);
    }
}
