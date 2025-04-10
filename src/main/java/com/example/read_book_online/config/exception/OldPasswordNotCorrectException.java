package com.example.read_book_online.config.exception;

public class OldPasswordNotCorrectException extends RuntimeException {
    public OldPasswordNotCorrectException(String message) {
        super(message);
    }
}
