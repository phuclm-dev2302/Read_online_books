package com.example.read_book_online.service.impl;

import com.example.read_book_online.config.exception.UserNotFoundException;
import com.example.read_book_online.dto.response.ResponseData;
import com.example.read_book_online.dto.response.UserResponse;
import com.example.read_book_online.entity.User;
import com.example.read_book_online.repository.UserRepository;
import com.example.read_book_online.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserRepository userRepository;

    @Override
    public User getUserBySecurity() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UserNotFoundException("User is not authenticated");
        }
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User Not Found"));

        return user;
    }


    @Override
    public ResponseData<UserResponse> getMe() {
        User user = getUserBySecurity();
        return new ResponseData<>(200,"Get user success", UserResponse.fromUser(user));
    }

    @Override
    public ResponseData<Page<UserResponse>> getUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> pageUsers = userRepository.findAll(pageable);
        Page<UserResponse> pageDTO = pageUsers.map(UserResponse::fromUser);

        return new ResponseData<>(200,"Get users success", pageDTO);
    }
}
