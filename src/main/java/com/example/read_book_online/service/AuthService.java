package com.example.read_book_online.service;


import com.example.read_book_online.dto.request.SignInRequest;
import com.example.read_book_online.dto.request.SignUpRequest;
import com.example.read_book_online.dto.response.AuthResponse;
import com.example.read_book_online.dto.response.ResponseData;


public interface AuthService {
    ResponseData<AuthResponse> login(SignInRequest form);
    ResponseData<String> register(SignUpRequest form);
//    ResponseData<String> logout(HttpServletRequest request, HttpServletResponse response);
    ResponseData<String> confirmUser(long userId, String otpCode);
}
