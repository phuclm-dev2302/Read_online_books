package com.example.read_book_online.dto.request;

import lombok.Data;

@Data
public class NotificationRequest {
    private Long  userId;
    private String title;
    private String body;
}