package com.example.read_book_online.service.impl;

import com.example.read_book_online.config.exception.*;
import com.example.read_book_online.dto.request.BookRequest;
import com.example.read_book_online.dto.response.BookResponse;
import com.example.read_book_online.dto.response.ResponseData;
import com.example.read_book_online.dto.response.ResponseError;
import com.example.read_book_online.entity.*;
import com.example.read_book_online.enums.VipStatusEnum;
import com.example.read_book_online.repository.*;
import com.example.read_book_online.service.BookService;
import com.example.read_book_online.service.CategoryService;
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
import java.time.LocalDate;
import java.util.List;
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
    @Autowired
    private VipMembershipRepository vipMembershipRepository;
    @Autowired
    private CategoryService categoryService;


    @Override
    public ResponseData<BookResponse> addBook(BookRequest bookRequest) {
        try {
            // Lấy danh sách category từ danh sách ID nhập vào
            List<Long> categoryIds = categoryService.getCategoryIdsAsList(bookRequest.getCategoryIds());
            List<Category> categories = categoryRepository.findAllById(categoryIds);

            // Kiểm tra xem có ID nào không tồn tại
            List<Long> foundCategoryIds = categories.stream().map(Category::getCategoryId).toList();
            List<Long> missingIds = categoryIds.stream()
                    .filter(id -> !foundCategoryIds.contains(id))
                    .toList();

            // Nếu có ID không hợp lệ, báo lỗi
            if (!missingIds.isEmpty()) {
                throw new RuntimeException("Category IDs not found: " + missingIds);
            }

            // theem ảnh cho sách
            String uploadedImagePath = null;
            if (bookRequest.getImage() != null && !bookRequest.getImage().isEmpty()) {
                Path uploadPath = Paths.get(System.getProperty("user.dir"), "uploads/bookImage");

                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                String fileName = System.currentTimeMillis() + "_" + bookRequest.getImage().getOriginalFilename();
                Path filePath = uploadPath.resolve(fileName);
                bookRequest.getImage().transferTo(filePath.toFile());

                // Đường dẫn để lưu vào database
                uploadedImagePath = "/uploads/bookImage/" + fileName;
            } else {
                log.warn("No image provided in the form.");
            }

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
                    .categories(categories)
                    .image(uploadedImagePath)
                    .pdfFilePath(filePath)
                    .createDate(LocalDate.now())
                    .isVip(bookRequest.getIsVip())
                    .interactions(null)
                    .build();

            bookRepository.save(book);

            return new ResponseData<>(200, "Book uploaded successfully", BookResponse.from(book, bookRepository));

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
        // Lấy thông tin sách
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        // Kiểm tra nếu sách yêu cầu VIP
        if (book.getIsVip()) {
            User user = userService.getUserBySecurity(); // Lấy user hiện tại
            VipMembership vipMembership = vipMembershipRepository.findByUserUserId(user.getUserId())
                    .orElseThrow(() -> new UserNotRegisteredVip("User is not a VIP member"));

            // Kiểm tra trạng thái VIP (phải là ACTIVE)
            if (vipMembership.getVipStatusEnum() != VipStatusEnum.ACTIVE) {
                throw new VipExpired("VIP membership is expired. Please renewal vip membership");
            }
        }

        // Kiểm tra nếu sách không có PDF
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
        Pageable pageable = PageRequest.of(page, size);

        Page<Book> books = bookRepository.findAll(pageable);
        if (books.isEmpty()) {
            throw new BookNotFoundException("Book not found");
        }

        Page<BookResponse> bookResponses = books.map(book -> BookResponse.from(book, bookRepository));
        return new ResponseData<>(200, "Books found", bookResponses);
    }

    @Override
    public ResponseData<String> addBookFavorite(Long bookId) {
        User user = userService.getUserBySecurity();
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        if (!user.getFavoriteBooks().contains(book)) {
            user.getFavoriteBooks().add(book);
            userRepository.save(user);
        }
        return new ResponseData<>(200, "add successfully");
    }

    @Override
    public ResponseData<String> removeBookFavorite(Long bookId) {
        User user = userService.getUserBySecurity();
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        if (user.getFavoriteBooks().contains(book)) {
            user.getFavoriteBooks().remove(book);
            userRepository.save(user);
        }
        return new ResponseData<>(200, "remove successfully");
    }

    @Override
    public ResponseData<List<BookResponse>> getFavoriteBooks() {
        User user = userService.getUserBySecurity();
        List<Book> favoriteBooks = user.getFavoriteBooks();

        if (favoriteBooks.isEmpty()) {
            return new ResponseData<>(200, "No favorite books found", null);
        }

        List<BookResponse> data = favoriteBooks.stream()
                .map(book -> BookResponse.from(book, bookRepository))
                .toList();

        return new ResponseData<>(200, "Get success", data);
    }
    @Override
    public ResponseData<List<BookResponse>> getLatestBooks() {
        List<Book> latestBooks = bookRepository.findLatestBooks();

        if (latestBooks.isEmpty()) {
            return new ResponseData<>(404, "No books found", null);
        }

        List<BookResponse> bookResponses = latestBooks.stream()
                .map(book -> BookResponse.from(book, bookRepository))
                .toList();

        return new ResponseData<>(200, "Latest books", bookResponses);
    }

    @Override
    public ResponseData<List<BookResponse>> getTopBooks() {
        List<Book> topBooks = bookRepository.findTopBooks();

        List<BookResponse> bookResponses = topBooks.stream()
                .map(book -> BookResponse.from(book, bookRepository))
                .toList();
        return new ResponseData<>(200, "Get success", bookResponses);
    }

    public ResponseData<List<BookResponse>> getSuggestedBooks() {
        User user = userService.getUserBySecurity();

        Pageable pageable = PageRequest.of(0, 10);
        List<Book> suggestedBooks = bookRepository.findSuggestedBooks(user.getUserId(),pageable);

        List<BookResponse> bookResponses = suggestedBooks.stream()
                .map(book -> BookResponse.from(book, bookRepository))
                .toList();
        return new ResponseData<>(200, "Suggested books fetched successfully", bookResponses);
    }
    @Override
    public ResponseData<List<BookResponse>> searchBookByCategoryNames(List<String> categoryNames) {
        List<Category> existingCategories = categoryRepository.findByCategoryNameIn(categoryNames);

        if (existingCategories.isEmpty()) {
            throw new CategoryNotFoundException("Category not found");
        }
        List<Book> books = bookRepository.findByCategoryNames(categoryNames);
        if (books.isEmpty()) {
            throw new BookNotFoundException("No books found in the selected category!");
        }
        List<BookResponse> bookResponses = books.stream()
                .map(book -> BookResponse.from(book, bookRepository))
                .toList();

        return new ResponseData<>(200,"search book by category name success",bookResponses);
    }

    @Override
    public ResponseData<List<BookResponse>> searchBookByAuthorNames(List<String> authorNames) {
        List<Author> existingAuthors = authorRepository.findByAuthorNameIn(authorNames);
        if (existingAuthors.isEmpty()) {
            throw new AuthorNotFoundException("Author not found");
        }
        List<Book> books = bookRepository.findByAuthorNames(authorNames);
        if (books.isEmpty()) {
            throw new BookNotFoundException("No books found in the selected author!");
        }
        List<BookResponse> bookResponses = books.stream()
                .map(book -> BookResponse.from(book,bookRepository))
                .toList();
        return new ResponseData<>(200,"search book by author name success", bookResponses);

    }
    @Override
    public ResponseData<List<BookResponse>> searchBooksByTitle(String title) {
        List<Book> books = bookRepository.findByTitleContainingIgnoreCase(title);

        if (books.isEmpty()) {
            throw new BookNotFoundException("Book not found with title: " + title);
        }

        List<BookResponse> bookResponses = books.stream()
                .map(book -> BookResponse.from(book, bookRepository))
                .toList();

        return new ResponseData<>(200, "seach book by title " + title + "success", bookResponses);
    }
    @Override
    public ResponseData<List<BookResponse>> getTopLikedBooks(int limit) {
        List<Book> books = bookRepository.findBooksByMostLikes(PageRequest.of(0, limit));

        if (books.isEmpty()) {
            throw new BookNotFoundException("Book not found with most likes");
        }

        List<BookResponse> bookResponses = books.stream()
                .map(book -> BookResponse.from(book, bookRepository))
                .toList();

        return new ResponseData<>(200, "get top liked books success", bookResponses);
    }

    @Override
    public ResponseData<List<BookResponse>> getTopViewedBooks(int limit) {
        List<Book> books = bookRepository.findBooksByMostViews(PageRequest.of(0, limit));

        if (books.isEmpty()) {
            throw new BookNotFoundException("Book not found with most views");
        }

        List<BookResponse> bookResponses = books.stream()
                .map(book -> BookResponse.from(book, bookRepository))
                .toList();

        return new ResponseData<>(200, "Get top viewed books success", bookResponses);
    }
}
