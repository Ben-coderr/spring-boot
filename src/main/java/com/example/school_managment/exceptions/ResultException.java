package com.example.school_managment.exceptions;

public class ResultException extends Exception {
    public ResultException(String message) {
        super(message);
    }

    public ResultException(String message, Throwable cause) {
        super(message, cause);
    }
}
