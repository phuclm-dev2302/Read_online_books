package com.example.read_book_online.service.impl;

import com.example.read_book_online.config.exception.UserNotFoundException;
import com.example.read_book_online.dto.request.ChangePasswordRequest;
import com.example.read_book_online.dto.request.UserRequest;
import com.example.read_book_online.dto.response.ResponseData;
import com.example.read_book_online.dto.response.ResponseError;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

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
    @Override
    public ResponseData<String> changePassword(ChangePasswordRequest changePasswordRequest) {
        User user = getUserBySecurity();

        if (changePasswordRequest.getOldPassword() == null || !passwordEncoder.matches(changePasswordRequest.getOldPassword(), user.getPassword())) {
            return new ResponseError<>(400, "Old password does not match");
        }

        if (changePasswordRequest.getNewPassword() == null || !changePasswordRequest.getNewPassword().equals(changePasswordRequest.getConfirmPassword())) {
            return new ResponseError<>(400, "New password does not match");
        }

        if (passwordEncoder.matches(changePasswordRequest.getNewPassword(), user.getPassword())) {
            return new ResponseError<>(400, "New password cannot be the same as old password");
        }

        user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        userRepository.save(user);

        return new ResponseData<>(200, "Change password success");
    }
    @Override
    public ResponseData<UserResponse> updateInfo(UserRequest userRequest) {
        User user = getUserBySecurity();

        user.setUsername(userRequest.getUserName());
        user.setDob(userRequest.getDob());
        user.setPhoneNumber(userRequest.getPhoneNumber());

        userRepository.save(user);

        return new ResponseData<>(200, "Update user success", UserResponse.fromUser(user));
    }
}
