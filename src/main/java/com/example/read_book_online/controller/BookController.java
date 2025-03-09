package com.example.read_book_online.controller;

import com.example.read_book_online.dto.request.BookRequest;
import com.example.read_book_online.dto.response.BookResponse;
import com.example.read_book_online.dto.response.ResponseData;
import com.example.read_book_online.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("api/v1/book")
public class BookController {
    @Autowired
    private BookService bookService;

    @Operation(summary = "Admin add new book", description = "API cho admin them mot quyen sach moi")
    @PostMapping("")
    public ResponseEntity<ResponseData<BookResponse>> addBook(@ModelAttribute BookRequest bookRequest) {
        return ResponseEntity.ok(bookService.addBook(bookRequest));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseData<BookResponse>> getBookById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(bookService.getBookById(id));
    }

    @PatchMapping("like/{id}")
    public ResponseEntity<ResponseData<BookResponse>> likeBook(@PathVariable("id") Long id) {
        return ResponseEntity.ok(bookService.likeBook(id));
    }

}
