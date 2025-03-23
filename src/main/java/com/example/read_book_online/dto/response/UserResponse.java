package com.example.read_book_online.dto.response;

import com.example.read_book_online.entity.User;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class UserResponse {
    private long userId;

    private String userName;

    private String email;

    private String phoneNumber;

    private LocalDate dob;

    private LocalDate createdDate;

    public static UserResponse fromUser(User user) {
        return UserResponse.builder()
                .userId(user.getUserId())
                .userName(user.getUsername())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .dob(user.getDob())
                .build();
    }

}
