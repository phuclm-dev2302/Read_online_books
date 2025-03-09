package com.example.read_book_online.service;

import com.example.read_book_online.dto.request.BookRequest;
import com.example.read_book_online.dto.response.BookResponse;
import com.example.read_book_online.dto.response.ResponseData;

public interface BookService {
    ResponseData<BookResponse> addBook(BookRequest bookRequest);
    ResponseData<BookResponse> getBookById(Long bookId);
    ResponseData<BookResponse> likeBook(Long bookId);
}
