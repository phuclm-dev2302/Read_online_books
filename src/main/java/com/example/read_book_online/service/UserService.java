package com.example.read_book_online.service;

import com.example.read_book_online.dto.response.ResponseData;
import com.example.read_book_online.dto.response.UserResponse;
import com.example.read_book_online.entity.User;

public interface UserService {
    User getUserBySecurity();
    ResponseData<UserResponse> getUser();

}
