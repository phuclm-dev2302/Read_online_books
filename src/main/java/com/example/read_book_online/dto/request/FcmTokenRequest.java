package com.example.read_book_online.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class FcmTokenRequest {

    @NotNull(message = "FCM Token cannot be null")
    @NotEmpty(message = "FCM Token cannot be empty")
    private String token;
}
