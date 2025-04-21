package com.example.read_book_online.controller;

import com.example.read_book_online.dto.request.FcmTokenRequest;
import com.example.read_book_online.dto.request.MulticastNotificationRequest;
import com.example.read_book_online.dto.request.NotificationRequest;
import com.example.read_book_online.dto.response.ResponseData;
import com.example.read_book_online.service.FcmService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notification")
public class NotificationController {

    @Autowired
    private FcmService fcmService;

    @PostMapping("/fcm-token")
    public ResponseEntity<ResponseData<String>> postNotification(@Valid @RequestBody FcmTokenRequest fcmTokenRequest) {
        return ResponseEntity.ok(fcmService.fcmTokenFromDevice(fcmTokenRequest.getToken()));
    }


    @PostMapping("/send")
    public ResponseEntity<?> sendNotification(@RequestBody NotificationRequest request) {
        String result = fcmService.sendNotificationToUser(request.getUserId(), request.getTitle(), request.getBody());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/send-multicast")
    public ResponseEntity<String> sendMulticast(@RequestBody MulticastNotificationRequest request) {
        String result = fcmService.sendMulticastNotification(
                request.getTokens(),
                request.getTitle(),
                request.getBody()
        );
        return ResponseEntity.ok(result);
    }

}
