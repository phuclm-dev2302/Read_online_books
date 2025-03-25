package com.example.read_book_online.repository;

import com.example.read_book_online.entity.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    Optional<Bookmark> findByUserUserIdAndBookBookId(Long userId, Long bookId);
    List<Bookmark> findByUserUserId(Long userId);
}
