package com.example.read_book_online.config.exception;

import com.example.read_book_online.dto.response.ResponseError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    // Xử lý lỗi khi thông tin không hợp lệ (validation)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    // Xử lý ngoại lệ UserNotFoundException (Khi không tìm thấy người dùng)
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ResponseError<Object>> handleUserNotFound(UserNotFoundException ex) {
        ResponseError<Object> errorResponse = new ResponseError<>(404, ex.getMessage());
        log.error("User not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(BookNotFoundException.class)      // book not found
    public ResponseEntity<ResponseError<Object>> handleBookNotFound(BookNotFoundException ex) {
        ResponseError<Object> errorResponse = new ResponseError<>(404, ex.getMessage());
        log.error("Book not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
}


