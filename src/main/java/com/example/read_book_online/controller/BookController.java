package com.example.read_book_online.controller;

import com.example.read_book_online.dto.request.BookRequest;
import com.example.read_book_online.dto.response.BookResponse;
import com.example.read_book_online.dto.response.ResponseData;
import com.example.read_book_online.entity.Book;
import com.example.read_book_online.repository.BookRepository;
import com.example.read_book_online.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("api/v1/book")
public class BookController {
    @Autowired
    private BookService bookService;
    @Autowired
    private BookRepository bookRepository;

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

    @GetMapping("/{id}/pdf")
    public ResponseEntity<Resource> getBookPDF(@PathVariable Long id) {
        try {
            Resource resource = bookService.getBookPDF(id);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"book.pdf\"") // Xem trực tiếp, filename là gi ý tên file khiusẻ tải xuốnh
                    .body(resource);
        } catch (RuntimeException | FileNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

}
