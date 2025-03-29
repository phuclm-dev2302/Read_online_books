package com.example.read_book_online.service;

import jakarta.mail.MessagingException;

import java.io.UnsupportedEncodingException;

public interface MaiiService {
    void sendConfirmLinkByKafka(String message) throws MessagingException, UnsupportedEncodingException;
}
