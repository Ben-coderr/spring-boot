package com.school.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


@ControllerAdvice
public class RestExceptionHandler {

    //this is an exception handler for response errors by spring boot 
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<String> handleStatus(ResponseStatusException ex) {
        return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
    }

    //this is an exception handle for bad url requests
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> badInput(MethodArgumentNotValidException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                             .body("invalid payload: " + ex.getMessage());
    }
}
