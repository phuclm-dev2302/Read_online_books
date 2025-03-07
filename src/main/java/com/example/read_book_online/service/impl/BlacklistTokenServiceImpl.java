package com.example.read_book_online.service.impl;


import com.example.read_book_online.entity.BlacklistToken;
import com.example.read_book_online.repository.BlacklistTokenRepository;
import com.example.read_book_online.service.BlacklistTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class BlacklistTokenServiceImpl implements BlacklistTokenService {

    @Autowired
    private BlacklistTokenRepository blacklistTokenRepository;

    @Override
    public void addTokenToBlacklist(String token, LocalDateTime expiryDate) { // Update the parameter type
        BlacklistToken blacklistToken = new BlacklistToken(token, expiryDate);
        blacklistTokenRepository.save(blacklistToken);
    }

    @Override
    public boolean isTokenBlacklisted(String token) {
        return blacklistTokenRepository.existsByToken(token);
    }
}
