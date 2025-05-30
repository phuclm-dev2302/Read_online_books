package com.example.read_book_online.service;

import com.example.read_book_online.dto.request.BookRequest;
import com.example.read_book_online.dto.response.BookResponse;
import com.example.read_book_online.dto.response.ResponseData;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;

import java.io.FileNotFoundException;
import java.util.List;

public interface BookService {
    ResponseData<BookResponse> addBook(BookRequest bookRequest);
    ResponseData<BookResponse> getBookById(Long bookId);
    ResponseData<BookResponse> likeBook(Long bookId);
    ResponseData<Page<BookResponse>> getBooks(int page, int size);
    Resource getBookPDF(Long bookId) throws FileNotFoundException;
    ResponseData<String> addBookFavorite(Long bookId);
    ResponseData<String> removeBookFavorite(Long bookId);
    ResponseData<List<BookResponse>> getFavoriteBooks ();
    ResponseData<List<BookResponse>> getLatestBooks();
    ResponseData<List<BookResponse>> getTopBooks();
    ResponseData<List<BookResponse>> getSuggestedBooks();
    ResponseData<List<BookResponse>> searchBookByCategoryNames(List<String> categoryNames);
    ResponseData<List<BookResponse>> searchBookByAuthorNames(List<String> authorNames);
    ResponseData<List<BookResponse>> searchBooksByTitle(String title);
    ResponseData<List<BookResponse>> getTopViewedBooks(int limit);
    ResponseData<List<BookResponse>> getTopLikedBooks(int limit);
}
