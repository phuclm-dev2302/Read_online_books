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

    @Query("SELECT b FROM Book b ORDER BY b.createDate DESC")
    List<Book> findLatestBooks();

    @Query("SELECT b FROM Book b " +
            "LEFT JOIN b.interactions i " +
            "GROUP BY b " +
            "ORDER BY SUM(i.views + CASE WHEN i.liked = true THEN 1 ELSE 0 END) DESC")
    List<Book> findTopBooks();

    @Query("""
            SELECT b FROM Book b
            JOIN b.categories c
            LEFT JOIN b.interactions bi
            WHERE c IN (
                SELECT c2 FROM User u
                JOIN u.favoriteBooks fb
                JOIN fb.categories c2
                WHERE u.userId = :userId
            )
            AND b NOT IN (
                SELECT fb FROM User u JOIN u.favoriteBooks fb WHERE u.userId = :userId
            )
            GROUP BY b
            ORDER BY 
              SUM(CASE WHEN bi.liked = true THEN 1 ELSE 0 END) DESC,
              SUM(bi.views) DESC
            """)
    List<Book> findSuggestedBooks(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT DISTINCT b FROM Book b JOIN b.categories c WHERE c.categoryName IN :categoryNames")
    List<Book> findByCategoryNames(@Param("categoryNames") List<String> categoryNames);

    @Query("SELECT b FROM Book b WHERE b.author.authorName IN :authorNames")
    List<Book> findByAuthorNames(@Param("authorNames") List<String> authorNames);

    List<Book> findByTitleContainingIgnoreCase(String title);

    @Query("SELECT b FROM Book b LEFT JOIN b.interactions i GROUP BY b ORDER BY SUM(CASE WHEN i.liked = true THEN 1 ELSE 0 END) DESC")
    List<Book> findBooksByMostLikes(Pageable pageable);

    @Query("SELECT b FROM Book b LEFT JOIN b.interactions i GROUP BY b ORDER BY SUM(i.views) DESC")
    List<Book> findBooksByMostViews(Pageable pageable);

}
