package com.example.read_book_online.service;

import com.example.read_book_online.dto.request.BookRequest;
import com.example.read_book_online.dto.response.BookResponse;
import com.example.read_book_online.dto.response.ResponseData;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;

import java.io.FileNotFoundException;

public interface BookService {
    ResponseData<BookResponse> addBook(BookRequest bookRequest);
    ResponseData<BookResponse> getBookById(Long bookId);
    ResponseData<BookResponse> likeBook(Long bookId);
    ResponseData<Page<BookResponse>> getBooks(int page, int size);
    Resource getBookPDF(Long bookId) throws FileNotFoundException;
}
