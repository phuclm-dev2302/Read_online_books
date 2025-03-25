package com.example.read_book_online.config.exception;

public class VipExpired extends RuntimeException {
    public VipExpired(String message) {
        super(message);
    }
}
