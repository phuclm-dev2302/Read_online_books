package com.example.read_book_online.service.impl;

import com.example.read_book_online.config.exception.NewPasswordNotDuplicated;
import com.example.read_book_online.config.exception.NewPasswordNotMatch;
import com.example.read_book_online.config.exception.OldPasswordNotCorrectException;
import com.example.read_book_online.config.exception.UserNotFoundException;
import com.example.read_book_online.dto.request.ChangePasswordRequest;
import com.example.read_book_online.dto.request.UserRequest;
import com.example.read_book_online.dto.response.ResponseData;
import com.example.read_book_online.dto.response.ResponseError;
import com.example.read_book_online.dto.response.UserResponse;
import com.example.read_book_online.entity.User;
import com.example.read_book_online.enums.StatusEnum;
import com.example.read_book_online.repository.UserRepository;
import com.example.read_book_online.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private KafkaTemplate kafkaTemplate;

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
    public ResponseData<UserResponse> banUser(Long id){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User Not Found"));

        String email = user.getEmail();
        user.setStatus(StatusEnum.BAN);
        userRepository.save(user);
        log.info("kafka send ban-account-topic");
        kafkaTemplate.send("ban-account-topic", email);

        log.info("User {} has been baned", email);
        return new ResponseData<>(201, "User has been banned", UserResponse.fromUser(user));

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
            throw new OldPasswordNotCorrectException("Old password does not match");
        }

        if (changePasswordRequest.getNewPassword() == null || !changePasswordRequest.getNewPassword().equals(changePasswordRequest.getConfirmPassword())) {
            throw new NewPasswordNotMatch("New password does not match");
        }

        if (passwordEncoder.matches(changePasswordRequest.getNewPassword(), user.getPassword())) {
            throw new NewPasswordNotDuplicated("New password cannot be the same as old password");
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
