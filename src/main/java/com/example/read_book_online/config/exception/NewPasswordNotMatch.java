package com.example.read_book_online.config.exception;

public class NewPasswordNotMatch extends RuntimeException {
    public NewPasswordNotMatch(String message) {
        super(message);
    }
}
