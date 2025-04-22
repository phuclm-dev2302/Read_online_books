package com.example.read_book_online.service.impl;

import com.example.read_book_online.dto.response.ResponseData;
import com.example.read_book_online.entity.FcmToken;
import com.example.read_book_online.entity.User;
import com.example.read_book_online.repository.FcmTokenRepository;
import com.example.read_book_online.service.FcmService;
import com.example.read_book_online.service.UserService;
import com.google.firebase.messaging.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FcmServiceImpl implements FcmService {

    @Autowired
    private UserService userService;
    @Autowired
    private FcmTokenRepository fcmTokenRepository;


    public ResponseData<String> fcmTokenFromDevice(String fcmToken) {

        User user = userService.getUserBySecurity();

        FcmToken existingToken = fcmTokenRepository.findByUser(user);

        if (existingToken != null) {
            existingToken.setToken(fcmToken);
            existingToken.setLastActive(LocalDateTime.now());
            fcmTokenRepository.save(existingToken);
            return new ResponseData<>(200,"success", "FCM Token update successfully!");
        } else {
            FcmToken newToken = FcmToken.builder()
                    .user(user)
                    .token(fcmToken)
                    .createdAt(LocalDateTime.now())
                    .lastActive(LocalDateTime.now())
                    .build();
            fcmTokenRepository.save(newToken);
            return new ResponseData<>(200,"success", "FCM Token created successfully!");

        }
    }

    public String sendNotificationToUser(Long  userId, String title, String body) {
        List<FcmToken> tokenList = fcmTokenRepository.findByUser_UserId(userId);

        if (tokenList.isEmpty()) {
            return "❌ Không tìm thấy token cho userId: " + userId;
        }

        StringBuilder result = new StringBuilder();
        for (FcmToken token : tokenList) {
            try {
                Message message = Message.builder()
                        .setToken(token.getToken())
                        .setNotification(Notification.builder()
                                .setTitle(title)
                                .setBody(body)
                                .build())
                        .build();

                String response = FirebaseMessaging.getInstance().send(message);
                result.append("✅ Gửi thành công: ").append(response).append("\n");

            } catch (Exception e) {
                result.append("❌ Gửi lỗi cho token: ").append(token.getToken()).append("\n");
                result.append("   Lỗi: ").append(e.getMessage()).append("\n");
            }
        }

        return result.toString();
    }

    public String sendMulticastNotification(List<String> tokens, String title, String body) {
        try {
            Notification notification = Notification.builder()
                    .setTitle(title)
                    .setBody(body)
                    .build();

            MulticastMessage message = MulticastMessage.builder()
                    .setNotification(notification)
                    .addAllTokens(tokens)
                    .build();

            BatchResponse response = FirebaseMessaging.getInstance().sendMulticast(message);

            return " Đã gửi " + response.getSuccessCount() + "/" + tokens.size() + " thông báo thành công.";
        } catch (Exception e) {
            e.printStackTrace();
            return " Lỗi khi gửi multicast: " + e.getMessage();
        }
    }
}
