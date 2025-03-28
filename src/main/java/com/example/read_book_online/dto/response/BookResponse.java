package com.example.read_book_online.dto.response;

import com.example.read_book_online.entity.Book;
import com.example.read_book_online.entity.BookInteraction;
import com.example.read_book_online.entity.Category;
import com.example.read_book_online.repository.BookRepository;
import lombok.Builder;
import lombok.Data;
import org.hibernate.boot.archive.scan.spi.ClassDescriptor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
public class BookResponse {
    private Long bookId;

    private String title;

    private List<String> categories;

    private String authorName;

    private String imagePath;

    private LocalDate createdAt;

    private String pdfFilePath;

    private Long totalViews;

    private Long totalLikes;

    public static BookResponse from(Book book, BookRepository bookRepository) {
        return BookResponse.builder()
                .bookId(book.getBookId())
                .authorName(book.getAuthor().getAuthorName())
                .title(book.getTitle())
                .imagePath(book.getImage())
                .createdAt(book.getCreateDate())
                .categories(book.getCategories().stream().map(Category::getCategoryName).toList())
                .pdfFilePath(book.getPdfFilePath())
                .totalLikes(bookRepository.countLikesByBookId(book.getBookId()))
                .totalViews(bookRepository.sumViewsByBookId(book.getBookId()))
                .build();
    }
}
