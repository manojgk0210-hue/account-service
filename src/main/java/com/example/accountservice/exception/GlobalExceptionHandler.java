package com.example.accountservice.exception;

import com.example.accountservice.dto.ErrorResponse;
import jakarta.persistence.OptimisticLockException;
import java.util.stream.Collectors;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ErrorResponse> validation(MethodArgumentNotValidException ex) {
        String fields = ex.getBindingResult().getFieldErrors().stream().map(e -> e.getField()).distinct().sorted().collect(Collectors.joining(", "));
        return response(400, "Bad Request", "Validation failed: " + fields);
    }
    @ExceptionHandler({HttpMessageNotReadableException.class, IllegalArgumentException.class})
    ResponseEntity<ErrorResponse> badRequest(Exception ex) { return response(400, "Bad Request", "Request contains invalid fields"); }
    @ExceptionHandler(AccountNotFoundException.class)
    ResponseEntity<ErrorResponse> notFound(AccountNotFoundException ex) { return response(404, "Not Found", ex.getMessage()); }
    @ExceptionHandler(InsufficientFundsException.class)
    ResponseEntity<ErrorResponse> insufficient(InsufficientFundsException ex) { return response(422, "Unprocessable Entity", ex.getMessage()); }
    @ExceptionHandler({OptimisticLockingFailureException.class, OptimisticLockException.class})
    ResponseEntity<ErrorResponse> conflict(Exception ex) { return response(409, "Conflict", "Concurrent update conflict"); }
    @ExceptionHandler(Exception.class)
    ResponseEntity<ErrorResponse> unexpected(Exception ex) { return response(500, "Internal Server Error", "An unexpected error occurred"); }
    private ResponseEntity<ErrorResponse> response(int status, String error, String message) {
        return ResponseEntity.status(status).body(new ErrorResponse(status, error, message));
    }
}
