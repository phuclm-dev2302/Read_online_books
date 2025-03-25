package com.example.read_book_online.repository;

import com.example.read_book_online.entity.Book;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query("SELECT DISTINCT b FROM Book b JOIN b.categories c WHERE c.categoryName IN :categoryNames")
    List<Book> findByCategoryNames(@Param("categoryNames") List<String> categoryNames);

    @Query("SELECT b FROM Book b WHERE b.author.authorName IN :authorNames")
    List<Book> findByAuthorNames(@Param("authorNames") List<String> authorNames);

    List<Book> findByTitleContainingIgnoreCase(String title);

    @Query("SELECT b FROM Book b JOIN b.interactions i GROUP BY b ORDER BY COUNT(i.liked) DESC")
    List<Book> findBooksByMostLikes(Pageable pageable);

    @Query("SELECT b FROM Book b JOIN b.interactions i GROUP BY b ORDER BY SUM(i.views) DESC")
    List<Book> findBooksByMostViews(Pageable pageable);

}
