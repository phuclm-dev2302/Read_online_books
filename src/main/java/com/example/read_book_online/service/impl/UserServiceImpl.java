package com.example.read_book_online.service.impl;

import com.example.read_book_online.config.exception.UserNotFoundException;
import com.example.read_book_online.entity.User;
import com.example.read_book_online.repository.UserRepository;
import com.example.read_book_online.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User Not Found"));

        return user;
    }
}
