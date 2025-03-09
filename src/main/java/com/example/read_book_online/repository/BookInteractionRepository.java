package com.example.read_book_online.repository;

import com.example.read_book_online.entity.Book;
import com.example.read_book_online.entity.BookInteraction;
import java.util.Optional;

import com.example.read_book_online.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookInteractionRepository extends JpaRepository<BookInteraction, Long> {
    // Đếm số lượt like của một sách
    @Query("SELECT COUNT(b) FROM BookInteraction b " +
            "WHERE b.book.bookId = :bookId AND b.liked = true")
    Long countLikesByBookId(Long bookId);

    // Tính tổng lượt xem của một sách
    @Query("SELECT SUM(b.views) FROM BookInteraction b " +
            "WHERE b.book.bookId = :bookId")
    Long sumViewsByBookId(Long bookId);

    @Query("SELECT bi FROM BookInteraction bi WHERE bi.user.userId = :userId AND bi.book.bookId = :bookId")
    Optional<BookInteraction> findByUserIdAndBookId(@Param("userId") Long userId, @Param("bookId") Long bookId);

}
