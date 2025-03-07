package com.example.read_book_online.service.impl;

import com.example.read_book_online.config.exception.BookNotFoundException;
import com.example.read_book_online.dto.request.BookRequest;
import com.example.read_book_online.dto.response.BookResponse;
import com.example.read_book_online.dto.response.ResponseData;
import com.example.read_book_online.entity.Author;
import com.example.read_book_online.entity.Book;
import com.example.read_book_online.entity.Category;
import com.example.read_book_online.repository.AuthorRepository;
import com.example.read_book_online.repository.BookRepository;
import com.example.read_book_online.repository.CategoryRepository;
import com.example.read_book_online.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class BookServiceImpl implements BookService {
    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/pdf/";
    @Autowired
    private AuthorRepository authorRepository;

    @Override
    public ResponseData<BookResponse> addBook(BookRequest bookRequest) {
        try {
            Category category = categoryRepository.findById(bookRequest.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));

            Author author = authorRepository.findById(bookRequest.getAuthorId())
                    .orElseThrow(() -> new RuntimeException("Author not found"));

            String filePath = null;

            // Kiểm tra nếu có file PDF
            if (bookRequest.getPdfFile() != null && !bookRequest.getPdfFile().isEmpty()) {
                MultipartFile file = bookRequest.getPdfFile();
                Path uploadPath = Paths.get(UPLOAD_DIR);

                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                Path fullPath = uploadPath.resolve(fileName);
                file.transferTo(fullPath.toFile());

                filePath = "/uploads/pdf/" + fileName; // Đường dẫn lưu vào database
            }

            Book book = Book.builder()
                    .title(bookRequest.getTitle())
                    .author(author)
                    .category(category)
                    .pdfFilePath(filePath)
                    .views(0L)
                    .likes(0L)
                    .build();

            bookRepository.save(book);

            return new ResponseData<>(200, "Book uploaded successfully",BookResponse.from(book));

        } catch (IOException e) {
            return new ResponseData<>(500, "Failed to upload PDF file", null);
        }
    }

    @Override
    public ResponseData<BookResponse> getBookById(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Book not found"));

        return new ResponseData<>(200, "Book found", BookResponse.from(book));
    }
}
