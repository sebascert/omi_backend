package com.example.omi;

import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException e) {
    String message =
        e.getBindingResult().getFieldErrors().stream()
            .findFirst()
            .map(err -> err.getField() + ": " + err.getDefaultMessage())
            .orElse("Invalid request body");

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(Map.of("error", "Bad Request", "message", message));
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<Map<String, Object>> handleDataIntegrity(
      DataIntegrityViolationException e) {
    String message = "Database constraint violation";

    Throwable root = e.getRootCause();
    if (root != null && root.getMessage() != null) {
      String rootMsg = root.getMessage();

      if (rootMsg.contains("ORA-02290")) {
        message = "Invalid value for one or more fields";
      } else if (rootMsg.contains("ORA-00001")) {
        message = "A unique value already exists";
      } else if (rootMsg.contains("ORA-02291") || rootMsg.contains("ORA-02292")) {
        message = "Referenced record does not exist or is still in use";
      }
    }

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(Map.of("error", "Bad Request", "message", message));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, Object>> handleOther(Exception e) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(
            Map.of(
                "error", "Internal Server Error",
                "message", "Unexpected server error"));
  }
}
