package com.jobmatrix.exceptionHandling;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<String> handleCategoryNotFoundException(CategoryNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(JobPostingNotFoundException.class)
    public ResponseEntity<String> handleJobPostingNotFoundException(JobPostingNotFoundException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
    // You can add more @ExceptionHandler methods here for other custom exceptions
}
