package com.school.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.school.exception.ApiException;


@ControllerAdvice
public class RestExceptionHandler {

    //this is an exception handler for response errors by spring boot
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<String> handleStatus(ResponseStatusException ex) {
        return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
    }

    //handle custom api exceptions
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<String> handleApi(ApiException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        if (ex.getClass().getSimpleName().contains("NotFound")) {
            status = HttpStatus.NOT_FOUND;
        }
        return ResponseEntity.status(status).body(ex.getMessage());
    }

    //this is an exception handle for bad url requests
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> badInput(MethodArgumentNotValidException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                             .body("invalid payload: " + ex.getMessage());
    }
}
