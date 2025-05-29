package com.example.school_managment.exceptions;

import com.example.school_managment.models.Teacher;

public class TeacherException extends Exception {
    public TeacherException(String message) {
        super(message);
    }

    public TeacherException(String message, Throwable cause) {
        super(message, cause);
    }
}
