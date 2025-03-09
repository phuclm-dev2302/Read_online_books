package com.example.read_book_online.dto.response;

import com.example.read_book_online.entity.Book;
import com.example.read_book_online.entity.BookInteraction;
import com.example.read_book_online.repository.BookRepository;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class BookResponse {
    private Long bookId;

    private String title;

    private String categoryName;

    private String authorName;

    private String pdfFilePath;

    private Long totalViews;

    private Long totalLikes;

    public static BookResponse from(Book book, BookRepository bookRepository) {
        return BookResponse.builder()
                .bookId(book.getBookId())
                .categoryName(book.getCategory().getCategoryName())
                .authorName(book.getAuthor().getAuthorName())
                .title(book.getTitle())
                .pdfFilePath(book.getPdfFilePath())
                .totalLikes(bookRepository.countLikesByBookId(book.getBookId()))
                .totalViews(bookRepository.sumViewsByBookId(book.getBookId()))
                .build();
    }
}
