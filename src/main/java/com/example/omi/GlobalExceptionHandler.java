package com.example.omi;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException e) {
    String message =
        e.getBindingResult().getFieldErrors().stream()
            .findFirst()
            .map(err -> err.getField() + ": " + err.getDefaultMessage())
            .orElse("Invalid request body");

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(Map.of("error", "Validation Failed", "message", message));
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException e) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(Map.of("error", "Invalid Argument", "message", e.getMessage()));
  }

  @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
  public ResponseEntity<Map<String, String>> handleDataIntegrity(Exception e) {
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(
            Map.of(
                "error",
                "Database Conflict",
                "message",
                "The operation could not be completed due to data constraints."));
  }

  @ExceptionHandler(org.springframework.dao.EmptyResultDataAccessException.class)
  public ResponseEntity<Map<String, String>> handleNotFoundData(
      org.springframework.dao.EmptyResultDataAccessException e) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(
            Map.of(
                "error", "Not Found",
                "message", "The requested resource does not exist"));
  }

  @ExceptionHandler(NoHandlerFoundException.class)
  public ResponseEntity<Map<String, String>> handleNoHandler(NoHandlerFoundException e) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(Map.of("error", "Not Found", "message", "Endpoint not found: " + e.getRequestURL()));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, String>> handleOther(Exception e) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(Map.of("error", "Internal Server Error", "message", "An unexpected error occurred."));
  }
}
