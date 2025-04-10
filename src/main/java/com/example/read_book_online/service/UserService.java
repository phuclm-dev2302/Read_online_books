package com.example.read_book_online.service;

import com.example.read_book_online.dto.request.ChangePasswordRequest;
import com.example.read_book_online.dto.request.UserRequest;
import com.example.read_book_online.dto.response.ResponseData;
import com.example.read_book_online.dto.response.UserResponse;
import com.example.read_book_online.entity.User;
import org.springframework.data.domain.Page;


public interface UserService {
    ResponseData<UserResponse> banUser(Long id);
    User getUserBySecurity();
    ResponseData<UserResponse> getMe();
    ResponseData<Page<UserResponse>> getUsers(int page, int size);
    ResponseData<String> changePassword(ChangePasswordRequest changePasswordRequest);
    ResponseData<UserResponse> updateInfo(UserRequest userRequest);
}
