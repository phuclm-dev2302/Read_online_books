package com.example.read_book_online.repository;

import com.example.read_book_online.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    @Query("SELECT COUNT(bi) FROM BookInteraction bi " +
            "WHERE bi.book.bookId = :bookId AND bi.liked = true")
    Long countLikesByBookId(@Param("bookId") Long bookId);

    @Query("SELECT SUM(bi.views) FROM BookInteraction bi" +
            " WHERE bi.book.bookId = :bookId")
    Long sumViewsByBookId(@Param("bookId") Long bookId);

}
