package com.example.read_book_online.service;


import com.example.read_book_online.dto.request.SignInRequest;
import com.example.read_book_online.dto.request.SignUpRequest;
import com.example.read_book_online.dto.response.AuthResponse;
import com.example.read_book_online.dto.response.ResponseData;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;


public interface AuthService {
    ResponseData<AuthResponse> login(SignInRequest form);
    ResponseData<String> register(SignUpRequest form);
    ResponseData<String> logout(HttpServletRequest request, HttpServletResponse response);
    ResponseData<String> confirmUser(long userId, String otpCode);
    ResponseData<AuthResponse> loginWithGoogle(String accessToken);
}
