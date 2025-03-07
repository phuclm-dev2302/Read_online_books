package com.example.read_book_online.dto.response;

import com.example.read_book_online.entity.Book;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class BookResponse {
    private Long bookId;

    private String title;

    private String categoryName;

    private String authorName;

    private String pdfFilePath;

    private Long views; // Tổng lượt xem

    private Long likes;

    public static BookResponse from(Book book) {
        return BookResponse.builder()
                .bookId(book.getBookId())
                .categoryName(book.getCategory().getCategoryName())
                .authorName(book.getAuthor().getAuthorName())
                .title(book.getTitle())
                .pdfFilePath(book.getPdfFilePath())
                .views(book.getViews())
                .likes(book.getLikes())
                .build();
    }
}
