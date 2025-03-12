package com.example.read_book_online.service.impl;

import com.example.read_book_online.config.exception.BookNotFoundException;
import com.example.read_book_online.dto.request.BookRequest;
import com.example.read_book_online.dto.response.BookResponse;
import com.example.read_book_online.dto.response.ResponseData;
import com.example.read_book_online.dto.response.ResponseError;
import com.example.read_book_online.entity.*;
import com.example.read_book_online.repository.*;
import com.example.read_book_online.service.BookService;
import com.example.read_book_online.service.UserService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Slf4j
@Service
public class BookServiceImpl implements BookService {
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/pdf/";
    @Autowired
    private AuthorRepository authorRepository;
    @Autowired
    private BookInteractionRepository bookInteractionRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;

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
                    .interactions(null)
                    .build();

            bookRepository.save(book);

            return new ResponseData<>(200, "Book uploaded successfully",BookResponse.from(book, bookRepository));

        } catch (IOException e) {
            return new ResponseData<>(500, "Failed to upload PDF file", null);
        }
    }

    @Override
    public ResponseData<BookResponse> getBookById(Long bookId) {
        User user = userService.getUserBySecurity();
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Book not found"));

        // Tìm BookInteraction xem user đã tương tác với book này chưa
        Optional<BookInteraction> optionalInteraction = bookInteractionRepository.findByUserIdAndBookId(user.getUserId(), book.getBookId());

        BookInteraction bookInteraction;
        if (optionalInteraction.isPresent()) {
            // Nếu đã có bản ghi, tăng số lượt xem
            bookInteraction = optionalInteraction.get();
            bookInteraction.setViews(bookInteraction.getViews() + 1);
        } else {
            bookInteraction = BookInteraction.builder()
                    .user(user)
                    .book(book)
                    .views(1) // Lượt xem đầu tiên
                    .liked(false) // Mặc định chưa like
                    .build();
        }

        bookInteractionRepository.save(bookInteraction);

        return new ResponseData<>(200, "Book found", BookResponse.from(book, bookRepository));
    }

    @Override
    public ResponseData<BookResponse> likeBook(Long bookId) {
        User user = userService.getUserBySecurity();
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Book not found"));

        Optional<BookInteraction> optionalInteraction = bookInteractionRepository.findByUserIdAndBookId(user.getUserId(), book.getBookId());
        if (optionalInteraction.isPresent()) {
            BookInteraction bookInteraction = optionalInteraction.get();
            if (bookInteraction.isLiked()) {
                log.debug("Disliked book id: " + bookId);
                bookInteraction.setLiked(false);
            } else {
                log.debug("Liked book id: " + bookId);
                bookInteraction.setLiked(true);
            }
            bookInteractionRepository.save(bookInteraction);
            return new ResponseData<>(200, "Success", BookResponse.from(book, bookRepository));
        } else {
            // Xử lý khi không tìm thấy BookInteraction
            return new ResponseData<>(404, "Book interaction not found", null);
        }
    }

    @Override
    public Resource getBookPDF(Long bookId) throws FileNotFoundException {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        if (book.getPdfFilePath() == null) {
            throw new RuntimeException("PDF file path is null");
        }

        // Đường dẫn file PDF
        Path filePath = Paths.get(System.getProperty("user.dir") + book.getPdfFilePath());
        File pdfFile = filePath.toFile();

        if (!pdfFile.exists()) {
            throw new RuntimeException("PDF file not found");
        }

        // Đọc file và truyền về dạng InputStreamResource
        return new InputStreamResource(new FileInputStream(pdfFile));
    }

    @Override
    public ResponseData<Page<BookResponse>> getBooks(int page, int size) {
        Pageable pageable = PageRequest.of(page,size);

        Page<Book> books = bookRepository.findAll(pageable);
        if (books.isEmpty()){
            throw new BookNotFoundException("Book not found");
        }

        Page<BookResponse> bookResponses = books.map(book -> BookResponse.from(book, bookRepository));
        return new ResponseData<>(200, "Books found", bookResponses);
    }
}
