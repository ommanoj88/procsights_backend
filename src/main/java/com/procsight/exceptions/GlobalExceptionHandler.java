package com.procsight.exceptions;

import com.google.firebase.auth.FirebaseAuthException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUserNotFoundException(
            UserNotFoundException ex, WebRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "success", false,
                "message", ex.getMessage(),
                "timestamp", LocalDateTime.now(),
                "path", request.getDescription(false)
        ));
    }

    @ExceptionHandler(CompanyNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleCompanyNotFoundException(
            CompanyNotFoundException ex, WebRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "success", false,
                "message", ex.getMessage(),
                "timestamp", LocalDateTime.now(),
                "path", request.getDescription(false)
        ));
    }

    @ExceptionHandler(InvoiceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleInvoiceNotFoundException(
            InvoiceNotFoundException ex, WebRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "success", false,
                "message", ex.getMessage(),
                "timestamp", LocalDateTime.now(),
                "path", request.getDescription(false)
        ));
    }

    @ExceptionHandler(FirebaseAuthException.class)
    public ResponseEntity<Map<String, Object>> handleFirebaseAuthException(
            FirebaseAuthException ex, WebRequest request) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                "success", false,
                "message", "Authentication failed: " + ex.getMessage(),
                "timestamp", LocalDateTime.now(),
                "path", request.getDescription(false)
        ));
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Map<String, Object>> handleUnauthorizedException(
            UnauthorizedException ex, WebRequest request) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                "success", false,
                "message", ex.getMessage(),
                "timestamp", LocalDateTime.now(),
                "path", request.getDescription(false)
        ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGlobalException(
            Exception ex, WebRequest request) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Internal server error: " + ex.getMessage(),
                "timestamp", LocalDateTime.now(),
                "path", request.getDescription(false)
        ));
    }
}