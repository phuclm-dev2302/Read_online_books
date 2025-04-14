package com.example.read_book_online.service;

public interface MomoPaymentService {
    String createPaymentRequest(String amount);
    String checkPaymentStatus(String orderId);
}