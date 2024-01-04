package com.drrr.global.controller;


import com.drrr.domain.common.AdminException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalRestControllerAdvice {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ExceptionResponse> constraintViolationException(ConstraintViolationException exception) {
        return ResponseEntity.badRequest()
                .body(new ExceptionResponse(0, exception.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExceptionResponse> illegalArgumentException(IllegalArgumentException exception) {
        return ResponseEntity.badRequest()
                .body(new ExceptionResponse(0, exception.getMessage()));
    }


    @ExceptionHandler(AdminException.class)
    public ResponseEntity<ExceptionResponse> adminException(AdminException exception) {
        return ResponseEntity.badRequest()
                .body(new ExceptionResponse(exception.getCode(), exception.getMessage()));
    }


    public record ExceptionResponse(
            int code,
            String message
    ) {

    }

}
