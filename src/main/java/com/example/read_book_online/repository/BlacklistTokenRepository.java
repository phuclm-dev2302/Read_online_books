package com.example.read_book_online.repository;

import com.example.read_book_online.entity.BlacklistToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface BlacklistTokenRepository extends JpaRepository<BlacklistToken, Long> {
    Optional<BlacklistToken> findByToken(String token);
    boolean existsByToken(String token);
}
