package com.example.read_book_online.service;

import com.example.read_book_online.dto.response.ResponseData;

import java.util.List;

public interface FcmService {
    String sendMulticastNotification(List<String> tokens, String title, String body);
    String sendNotificationToUser(Long userId, String title, String body);
    ResponseData<String> fcmTokenFromDevice(String fcmToken);
}
