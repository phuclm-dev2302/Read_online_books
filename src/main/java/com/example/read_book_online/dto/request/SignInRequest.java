package com.example.read_book_online.dto.request;

import lombok.Data;

@Data
public class SignInRequest {
    private String email;
    private String password;
}