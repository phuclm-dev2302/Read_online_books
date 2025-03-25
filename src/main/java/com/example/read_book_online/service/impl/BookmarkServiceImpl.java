package com.example.read_book_online.service.impl;

import com.example.read_book_online.config.exception.BookNotFoundException;
import com.example.read_book_online.dto.request.BookmarkRequest;
import com.example.read_book_online.dto.response.BookResponse;
import com.example.read_book_online.dto.response.BookmarkResponse;
import com.example.read_book_online.dto.response.ResponseData;
import com.example.read_book_online.entity.Book;
import com.example.read_book_online.entity.Bookmark;
import com.example.read_book_online.entity.User;
import com.example.read_book_online.repository.BookRepository;
import com.example.read_book_online.repository.BookmarkRepository;
import com.example.read_book_online.repository.UserRepository;
import com.example.read_book_online.service.BookmarkService;
import com.example.read_book_online.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class BookmarkServiceImpl implements BookmarkService {

    @Autowired
    private BookmarkRepository bookmarkRepository;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;

    @Override
    public ResponseData<BookmarkResponse> addBookmark(BookmarkRequest bookmarkRequest) {

        User user = userService.getUserBySecurity();
        Book book = bookRepository.findById(bookmarkRequest.getBookId())
                .orElseThrow(() -> new BookNotFoundException("Book not found"));

        Bookmark bookmark = bookmarkRepository.findByUserUserIdAndBookBookId(user.getUserId(), bookmarkRequest.getBookId())
                .orElse(Bookmark.builder()
                        .user(user)
                        .book(book)
                        .pageNumber(bookmarkRequest.getPage())
                        .build());

        bookmark.setPageNumber(bookmarkRequest.getPage());
        bookmarkRepository.save(bookmark);

        return new ResponseData<>(200, "addBookmark success");
    }

    @Override
    public ResponseData<String> deleteBookmark(Long bookmarkId) {
        Bookmark bookmark = bookmarkRepository.findById(bookmarkId)
                .orElseThrow(() -> new RuntimeException("Bookmark not found"));

        User user = userService.getUserBySecurity();
        if (!bookmark.getUser().getUserId().equals(user.getUserId())) {
            throw new RuntimeException("You do not have permission to delete this bookmark");
        }
        bookmarkRepository.delete(bookmark);
        return new ResponseData<>(200, "deleteBookmark success");
    }

    @Override
    public ResponseData<List<BookmarkResponse>> getMyBookmarks() {
        User user = userService.getUserBySecurity();

        List<Bookmark> bookmarks = bookmarkRepository.findByUserUserId(user.getUserId());

        if (bookmarks == null || bookmarks.isEmpty()) {
            return new ResponseData<>(200, "Empty Bookmark list");
        }

        List<BookmarkResponse> data = bookmarks.stream()
                .map(bookmark -> BookmarkResponse.from(bookmark,bookmarkRepository,bookRepository))
                .toList();

        return new ResponseData<>(200, "getMyBookmarks success", data);
    }
}
