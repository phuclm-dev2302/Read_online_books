package com.example.read_book_online.repository;

import com.example.read_book_online.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    @Query("SELECT COUNT(bi) FROM BookInteraction bi " +
            "WHERE bi.book.bookId = :bookId AND bi.liked = true")
    Long countLikesByBookId(@Param("bookId") Long bookId);

    @Query("SELECT SUM(bi.views) FROM BookInteraction bi" +
            " WHERE bi.book.bookId = :bookId")
    Long sumViewsByBookId(@Param("bookId") Long bookId);

    @Query("SELECT b FROM Book b ORDER BY b.createDate DESC")
    List<Book> findLatestBooks();

    @Query("SELECT b FROM Book b " +
            "LEFT JOIN b.interactions i " +
            "GROUP BY b " +
            "ORDER BY SUM(i.views + CASE WHEN i.liked = true THEN 1 ELSE 0 END) DESC")
    List<Book> findTopBooks();

    @Query("SELECT b FROM Book b " +
            "JOIN b.categories c " +
            "WHERE c IN (SELECT c FROM User u " +
            "JOIN u.favoriteBooks fb " +
            "JOIN fb.categories c " +
            "WHERE u.userId = :userId) " +
            "AND b NOT IN (SELECT fb FROM User u JOIN u.favoriteBooks fb WHERE u.userId = :userId) " +
            "GROUP BY b " +
            "ORDER BY COUNT((SELECT bi FROM BookInteraction bi WHERE bi.book = b AND bi.liked = true)) DESC, " +
            "SUM((SELECT bi.views FROM BookInteraction bi WHERE bi.book = b)) DESC")
    List<Book> findSuggestedBooks(@Param("userId") Long userId, Pageable pageable);



}
