package com.example.read_book_online.controller;

import com.example.read_book_online.dto.request.BookRequest;
import com.example.read_book_online.dto.request.BookmarkRequest;
import com.example.read_book_online.dto.response.BookResponse;
import com.example.read_book_online.dto.response.BookmarkResponse;
import com.example.read_book_online.dto.response.ResponseData;
import com.example.read_book_online.service.BookService;
import com.example.read_book_online.service.BookmarkService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.core.io.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.util.List;


@RestController
@RequestMapping("api/v1/book")
public class BookController {
    @Autowired
    private BookService bookService;
    @Autowired
    private BookmarkService bookmarkService;

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
        } catch (FileNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping()
    public ResponseEntity<ResponseData<Page<BookResponse>>> getBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        ResponseData<Page<BookResponse>> result = bookService.getBooks(page, size);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @Operation(summary = "Users add books to favorites ", description = "API cho người dung thêm vào mục yêu thích")
    @PostMapping("/favorite/add/{id}")
    public  ResponseEntity<ResponseData<String>> addFavouriteBook (@PathVariable("id") Long id) {
        return ResponseEntity.ok(bookService.addBookFavorite(id));
    }

    @Operation(summary = "User deletes book from favorites", description = "API cho người dùng xoá sách khỏi mục yêu thích")
    @PostMapping("/favorite/remove/{id}")
    public ResponseEntity<ResponseData<String>> RemoveFavouriteBook (@PathVariable("id") Long id) {
        return ResponseEntity.ok(bookService.removeBookFavorite(id));
    }

    @Operation(summary = "User view their wishlist ", description = "API cho ngươời dùng xem danh sách yêu  thích của họ")
    @GetMapping("/favorite")
    public ResponseEntity<ResponseData<List<BookResponse>>> getMyFavouriteBooks() {
        return ResponseEntity.ok(bookService.getFavoriteBooks());
    }

    @Operation(summary = "User add a bookmark", description = "API cho người dùng thêm đánh dấu vào sách")
    @PostMapping("/bookmark/add")
    public ResponseEntity<ResponseData<BookmarkResponse>> bookmarkBook(@RequestBody BookmarkRequest bookmarkRequest) {
        return ResponseEntity.ok(bookmarkService.addBookmark(bookmarkRequest));
    }

    @Operation(summary = "User delete bookmark", description = "API cho người dùng xoã đánh dấu")
    @DeleteMapping("/bookmark/remove/{id}")
    public ResponseEntity<ResponseData<String>> deleteBookmarkBook(@PathVariable("id") Long id) {
        return ResponseEntity.ok(bookmarkService.deleteBookmark(id));
    }

    @Operation(summary = "User view all their bookmark", description = "API cho người dùng xem tất cả các sách đã đaánh dấu")
    @GetMapping("bookmark/me")
    public ResponseEntity<ResponseData<List<BookmarkResponse>>> getMyBookmarks() {
        return ResponseEntity.ok(bookmarkService.getMyBookmarks());
    }

    @GetMapping("/latest")
    public ResponseEntity<ResponseData<List<BookResponse>>> getLatestBooks() {
        return ResponseEntity.ok(bookService.getLatestBooks());
    }

//    @GetMapping("/top")
//    public ResponseEntity<ResponseData<List<BookResponse>>> getTopBooks() {
//        return ResponseEntity.ok(bookService.getTopBooks());
//    }

    @GetMapping("/suggested")
    public ResponseEntity<ResponseData<List<BookResponse>>> getSuggestedBooks() {
        return ResponseEntity.ok(bookService.getSuggestedBooks());
    }


}
