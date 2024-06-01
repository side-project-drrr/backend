package com.drrr.error;

import com.drrr.core.exception.BaseCustomException;
import com.drrr.domain.exception.DomainExceptionCode;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(BaseCustomException.class)
    public ResponseEntity<ErrorResponse> handleException(BaseCustomException e) {
        log.error("{}", e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(e.getCode(), e.getMessage()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> ServerException(RuntimeException e) {
        log.error("{}", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.of(500, e.getMessage()));
    }

    @ExceptionHandler(BadJwtException.class)
    public ResponseEntity<ErrorResponse> BadJwtException(BadJwtException e) {
        log.error("{}", e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(DomainExceptionCode.JWT_TOKEN_INVALID.getCode(),
                        DomainExceptionCode.JWT_TOKEN_INVALID.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> ArgumentException(IllegalArgumentException e) {
        log.error("{}", e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(400, e.getMessage()));
    }

    @ExceptionHandler(PropertyReferenceException.class)
    public ResponseEntity<ErrorResponse> PropertyException(PropertyReferenceException e) {
        log.error("{}", e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(400, e.getMessage()));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> constraintViolationException(ConstraintViolationException e) {
        log.error("{}", e);
        return ResponseEntity.badRequest()
                .body(ErrorResponse.of(0, e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> constraintViolationException(MethodArgumentNotValidException e) {
        log.error("{}", e);
        return ResponseEntity.badRequest()
                .body(ErrorResponse.of(0, e.getMessage()));
    }

}
