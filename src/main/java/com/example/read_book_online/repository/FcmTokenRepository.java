package com.example.read_book_online.repository;

import com.example.read_book_online.entity.FcmToken;
import com.example.read_book_online.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface FcmTokenRepository extends JpaRepository<FcmToken, Long> {
    FcmToken findByUser(User user);
    List<FcmToken> findByUser_UserId(Long userId);
}
