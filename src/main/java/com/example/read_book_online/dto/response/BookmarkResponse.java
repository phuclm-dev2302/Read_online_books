package com.example.read_book_online.dto.response;

import com.example.read_book_online.entity.Book;
import com.example.read_book_online.entity.Bookmark;
import com.example.read_book_online.entity.Category;
import com.example.read_book_online.repository.BookmarkRepository;
import com.example.read_book_online.repository.BookRepository;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Builder
@Data
public class BookmarkResponse {
    private Long bookmarkId;

    private Long bookId;

    private String title;

    private List<String> categories;

    private String authorName;

    private LocalDate createdAt;

    private String pdfFilePath;

    private Long totalViews;

    private Long totalLikes;

    private Long pageNumber;

    public static BookmarkResponse from(Bookmark bookmark, BookmarkRepository bookmarkRepository, BookRepository bookRepository) {
        return BookmarkResponse.builder()
                .bookmarkId(bookmark.getId())
                .bookId(bookmark.getBook().getBookId())
                .authorName(bookmark.getBook().getAuthor().getAuthorName())
                .title(bookmark.getBook().getTitle())
                .createdAt(bookmark.getBook().getCreateDate())
                .categories(bookmark.getBook().getCategories().stream().map(Category::getCategoryName).toList())
                .pdfFilePath(bookmark.getBook().getPdfFilePath())
                .totalLikes(bookRepository.countLikesByBookId(bookmark.getBook().getBookId()))
                .totalViews(bookRepository.sumViewsByBookId(bookmark.getBook().getBookId()))
                .pageNumber(bookmark.getPageNumber())
                .build();
    }
}
