package com.school.common.exception;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleNotFound(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
    
    @ExceptionHandler(CapacityExceededException.class)
    public ResponseEntity<String> handleCapacity(CapacityExceededException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)   // 409
                             .body(ex.getMessage());
    }
    
}
